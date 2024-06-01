package com.odde.doughnut.testability;

import static com.odde.doughnut.services.ai.builder.OpenAIChatRequestBuilder.askClarificationQuestion;
import static com.odde.doughnut.services.ai.tools.AiToolFactory.COMPLETE_NOTE_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.run.*;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.assistants.message.Message;
import com.theokanning.openai.assistants.message.MessageContent;
import com.theokanning.openai.assistants.message.content.Text;
import io.reactivex.Single;
import java.util.List;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public record OpenAIAssistantMock(OpenAiApi openAiApi) {

  public void mockThreadRunCompletionToolCalled(Object result, String runId) {
    mockCreateRunInProcess(runId);
    Run retrievedRun = getRunThatCallCompletionTool(runId, result);
    Mockito.doReturn(Single.just(retrievedRun))
        .when(openAiApi)
        .retrieveRun(ArgumentMatchers.any(), ArgumentMatchers.any());
  }

  public void mockThreadRunCompletedAndListMessage(String msg, String runId) {
    mockCreateRunInProcess(runId);
    Run retrievedRun = getRunThatCompleted(runId);
    Mockito.doReturn(Single.just(retrievedRun))
        .when(openAiApi)
        .retrieveRun(ArgumentMatchers.any(), ArgumentMatchers.any());
    Text txt = new Text(msg, List.of());
    MessageContent cnt = new MessageContent();
    cnt.setText(txt);
    List<MessageContent> contentList = List.of(cnt);
    OpenAiResponse<Message> msgs = new OpenAiResponse<>();
    msgs.setData(List.of(Message.builder().content(contentList).build()));
    Mockito.doReturn(Single.just(msgs)).when(openAiApi).listMessages(retrievedRun.getThreadId(), null);
  }

  public void mockSubmitOutputAndCompletion(Object result, String runId) {
    Run run = getRunThatCallCompletionTool(runId, result);
    when(openAiApi.submitToolOutputs(any(), any(), any())).thenReturn(Single.just(run));
  }

  public void mockSubmitOutputAndRequiredMoreAction(Object result, String runId) {
    Run run =
        getRunThatRequiresAction(
            new ObjectMapper().valueToTree(result), runId, askClarificationQuestion);
    when(openAiApi.submitToolOutputs(any(), any(), any())).thenReturn(Single.just(run));
  }

  private static Run getRunThatCallCompletionTool(String runId, Object result) {
    JsonNode arguments = new ObjectMapper().valueToTree(result);
    return getRunThatRequiresAction(arguments, runId, COMPLETE_NOTE_DETAILS);
  }

  private void mockCreateRunInProcess(String runId) {
    Run run = new Run();
    run.setId(runId);
    run.setStatus("processing");
    Mockito.doReturn(Single.just(run))
        .when(openAiApi)
        .createRun(ArgumentMatchers.any(), ArgumentMatchers.any());
  }

  private static Run getRunThatCompleted(String runId) {
    Run retrievedRun = new Run();
    retrievedRun.setId(runId);
    retrievedRun.setStatus("completed");
    return retrievedRun;
  }

  private static Run getRunThatRequiresAction(
      JsonNode arguments, String runId, String function_name) {
    Run retrievedRun = new Run();
    retrievedRun.setId(runId);
    retrievedRun.setStatus("requires_action");
    retrievedRun.setRequiredAction(
        RequiredAction.builder()
            .submitToolOutputs(
                SubmitToolOutputs.builder()
                    .toolCalls(
                        List.of(
                            ToolCall.builder()
                                .id("mocked-tool-call-id")
                                .function(
                                    ToolCallFunction.builder()
                                        .name(function_name)
                                        .arguments(arguments)
                                        .build())
                                .build()))
                    .build())
            .build());
    return retrievedRun;
  }
}
