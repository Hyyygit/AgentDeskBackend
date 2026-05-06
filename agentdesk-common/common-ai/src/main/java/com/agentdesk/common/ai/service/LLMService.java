package com.agentdesk.common.ai.service;

import com.agentdesk.common.ai.model.ChatMessage;
import com.agentdesk.common.ai.model.ChatRequest;
import com.agentdesk.common.ai.model.ChatResponse;

import java.util.List;
import java.util.function.Consumer;

public interface LLMService {

    ChatResponse chat(ChatRequest request);

    ChatResponse chat(List<ChatMessage> messages);

    void chatStream(ChatRequest request, Consumer<String> deltaConsumer);
}
