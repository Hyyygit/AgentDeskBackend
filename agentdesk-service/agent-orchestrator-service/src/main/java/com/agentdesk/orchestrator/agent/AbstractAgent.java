package com.agentdesk.orchestrator.agent;

import com.agentdesk.common.ai.config.LLMConfig;
import com.agentdesk.common.ai.model.ChatMessage;
import com.agentdesk.common.ai.model.ChatRequest;
import com.agentdesk.common.ai.model.ChatResponse;
import com.agentdesk.common.ai.service.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractAgent {
    protected static final Logger log = LoggerFactory.getLogger(AbstractAgent.class);

    @Autowired
    protected LLMService llmService;

    @Autowired
    protected LLMConfig llmConfig;

    public abstract String agentName();

    protected String callLLM(String systemPrompt, String userPrompt) {
        List<ChatMessage> messages = List.of(
            ChatMessage.system(systemPrompt),
            ChatMessage.user(userPrompt)
        );
        ChatRequest request = new ChatRequest();
        request.setMessages(messages);
        ChatResponse response = llmService.chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
