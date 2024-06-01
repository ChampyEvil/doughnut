package com.odde.doughnut.services.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.completion.chat.AssistantMessage;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ChatMessageForFineTuning {

  @NonNull String role;
  String content;

  @JsonProperty("function_call")
  ChatFunctionCallForFineTuning functionCall;

  public static ChatMessageForFineTuning from(ChatMessage chatMessage) {
    var chatMessageForFineTuning = new ChatMessageForFineTuning();
    chatMessageForFineTuning.role = chatMessage.getRole();
    chatMessageForFineTuning.content = chatMessage.getTextContent();
    if (chatMessage instanceof AssistantMessage assistantMessage && assistantMessage.getFunctionCall() != null) {
      chatMessageForFineTuning.functionCall =
          ChatFunctionCallForFineTuning.from(assistantMessage.getFunctionCall());
    }

    return chatMessageForFineTuning;
  }
}
