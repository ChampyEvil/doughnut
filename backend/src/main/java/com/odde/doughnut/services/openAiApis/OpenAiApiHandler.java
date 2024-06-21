package com.odde.doughnut.services.openAiApis;

import static com.odde.doughnut.services.openAiApis.ApiExecutor.blockGet;
import static java.lang.Thread.sleep;

import com.fasterxml.jackson.databind.JsonNode;
import com.odde.doughnut.controllers.dto.AiCompletionAnswerClarifyingQuestionParams;
import com.odde.doughnut.controllers.dto.SrtDto;
import com.odde.doughnut.exceptions.OpenAIServiceErrorException;
import com.odde.doughnut.services.ai.OpenAIChatGPTFineTuningExample;
import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.assistants.assistant.AssistantRequest;
import com.theokanning.openai.assistants.message.Message;
import com.theokanning.openai.assistants.message.MessageRequest;
import com.theokanning.openai.assistants.run.Run;
import com.theokanning.openai.assistants.run.RunCreateRequest;
import com.theokanning.openai.assistants.run.SubmitToolOutputRequestItem;
import com.theokanning.openai.assistants.run.SubmitToolOutputsRequest;
import com.theokanning.openai.assistants.thread.Thread;
import com.theokanning.openai.assistants.thread.ThreadRequest;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.fine_tuning.FineTuningJob;
import com.theokanning.openai.fine_tuning.FineTuningJobRequest;
import com.theokanning.openai.fine_tuning.Hyperparameters;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.model.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.http.HttpStatus;

public class OpenAiApiHandler {
  private final OpenAiApi openAiApi;

  public OpenAiApiHandler(OpenAiApi openAiApi) {
    this.openAiApi = openAiApi;
  }

  public String getOpenAiImage(String prompt) {
    CreateImageRequest completionRequest =
        CreateImageRequest.builder().prompt(prompt).responseFormat("b64_json").build();
    ImageResult choices = blockGet(openAiApi.createImage(completionRequest));

    return choices.getData().get(0).getB64Json();
  }

  public Optional<JsonNode> getFirstToolCallArguments(ChatCompletionRequest chatRequest) {
    return getFirstToolCalls(chatRequest)
        .map(ChatToolCall::getFunction)
        .map(ChatFunctionCall::getArguments);
  }

  private Optional<ChatToolCall> getFirstToolCalls(ChatCompletionRequest chatRequest) {
    return chatCompletion(chatRequest)
        //        .map(x->{
        //          System.out.println(chatRequest);
        //          System.out.println(x);
        //          return x;
        //        })
        .map(ChatCompletionChoice::getMessage)
        .map(AssistantMessage::getToolCalls)
        .flatMap(x -> x.stream().findFirst());
  }

  public Optional<ChatCompletionChoice> chatCompletion(ChatCompletionRequest request) {
    return blockGet(openAiApi.createChatCompletion(request)).getChoices().stream().findFirst();
  }

  public List<Model> getModels() {
    return blockGet(openAiApi.listModels()).data;
  }

  public String uploadFineTuningExamples(
      List<OpenAIChatGPTFineTuningExample> examples, String subFileName) throws IOException {
    FineTuningFileWrapper uploader = new FineTuningFileWrapper(examples, subFileName);
    return uploader.withFileToBeUploaded(
        (file) -> {
          RequestBody purpose = RequestBody.create("fine-tune", MediaType.parse("text/plain"));
          try {
            return blockGet(openAiApi.uploadFile(purpose, file)).getId();
          } catch (Exception e) {
            throw new OpenAIServiceErrorException(
                "Upload failed.", HttpStatus.INTERNAL_SERVER_ERROR);
          }
        });
  }

  public FineTuningJob triggerFineTuning(String fileId) {
    FineTuningJobRequest fineTuningJobRequest = new FineTuningJobRequest();
    fineTuningJobRequest.setTrainingFile(fileId);
    fineTuningJobRequest.setModel("gpt-3.5-turbo-1106");
    fineTuningJobRequest.setHyperparameters(
        new Hyperparameters()); // not sure what should be the nEpochs value

    FineTuningJob fineTuningJob = blockGet(openAiApi.createFineTuningJob(fineTuningJobRequest));
    if (List.of("failed", "cancelled").contains(fineTuningJob.getStatus())) {
      throw new OpenAIServiceErrorException(
          "Trigger Fine-Tuning Failed: " + fineTuningJob, HttpStatus.BAD_REQUEST);
    }
    return fineTuningJob;
  }

  public Assistant createAssistant(AssistantRequest assistantRequest) {
    return blockGet(openAiApi.createAssistant(assistantRequest));
  }

  public Thread createThread(ThreadRequest threadRequest) {
    return blockGet(openAiApi.createThread(threadRequest));
  }

  public void createMessage(String threadId, MessageRequest messageRequest) {
    blockGet(openAiApi.createMessage(threadId, messageRequest));
  }

  private Run retrieveRun(String threadId, String runId) {
    return blockGet(openAiApi.retrieveRun(threadId, runId));
  }

  public Run createRun(String threadId, String assistantId) {
    RunCreateRequest runCreateRequest = RunCreateRequest.builder().assistantId(assistantId).build();
    return blockGet(openAiApi.createRun(threadId, runCreateRequest));
  }

  public Run retrieveUntilCompletedOrRequiresAction(String threadId, Run currentRun) {
    Run retrievedRun = currentRun;
    int count = 0;
    while (!(retrievedRun.getStatus().equals("completed"))
        && !(retrievedRun.getStatus().equals("failed"))
        && !(retrievedRun.getStatus().equals("requires_action"))) {
      count++;
      if (count > 15) {
        break;
      }
      wait(count - 1);
      retrievedRun = retrieveRun(threadId, currentRun.getId());
    }
    if (retrievedRun.getStatus().equals("requires_action")
        || retrievedRun.getStatus().equals("completed")) {
      return retrievedRun;
    }
    throw new RuntimeException("OpenAI run status: " + retrievedRun.getStatus());
  }

  private static void wait(int hundredMilliSeconds) {
    try {
      sleep(hundredMilliSeconds * 200L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public Run submitToolOutputs(
      AiCompletionAnswerClarifyingQuestionParams answerClarifyingQuestionParams) {
    SubmitToolOutputRequestItem toolOutputRequestItem =
        SubmitToolOutputRequestItem.builder()
            .toolCallId(answerClarifyingQuestionParams.getToolCallId())
            .output(answerClarifyingQuestionParams.getAnswer())
            .build();
    List<SubmitToolOutputRequestItem> toolOutputRequestItems = new ArrayList<>();
    toolOutputRequestItems.add(toolOutputRequestItem);
    SubmitToolOutputsRequest submitToolOutputsRequest =
        SubmitToolOutputsRequest.builder().toolOutputs(toolOutputRequestItems).build();
    return blockGet(
        openAiApi.submitToolOutputs(
            answerClarifyingQuestionParams.getThreadId(),
            answerClarifyingQuestionParams.getRunId(),
            submitToolOutputsRequest));
  }

  public List<Message> getThreadLastMessage(String threadId) {
    return blockGet(openAiApi.listMessages(threadId, Map.of("order", "asc"))).getData();
  }

  public SrtDto getTranscription(RequestBody requestBody) throws IOException {
    String string =
        blockGet(((OpenAiApiExtended) openAiApi).createTranscriptionSrt(requestBody)).string();
    SrtDto srtDto = new SrtDto();
    srtDto.setSrt(string);
    return srtDto;
  }
}
