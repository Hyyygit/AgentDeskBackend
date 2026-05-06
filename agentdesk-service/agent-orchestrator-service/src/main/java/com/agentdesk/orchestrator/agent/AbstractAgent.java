package com.agentdesk.orchestrator.agent;

import com.agentdesk.common.ai.model.ChatMessage;
import com.agentdesk.common.ai.model.ChatRequest;
import com.agentdesk.common.ai.config.LLMConfig;
import com.agentdesk.common.ai.service.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractAgent {
    protected static final Logger log = LoggerFactory.getLogger(AbstractAgent.class);

    @Autowired
    protected LLMService llmService;

    @Autowired
    protected LLMConfig llmConfig;

    @Autowired(required = false)
    protected ChatModel chatModel;

    public abstract String agentName();

    protected String callLLM(String systemPrompt, String userPrompt) {
        if (chatModel != null) {
            try {
                Message systemMsg = new SystemMessage(systemPrompt);
                Message userMsg = new UserMessage(userPrompt);
                Prompt prompt = new Prompt(List.of(systemMsg, userMsg));
                ChatResponse response = chatModel.call(prompt);
                return response.getResult().getOutput().getText();
            } catch (Exception e) {
                log.warn("Spring AI ChatModel call failed, falling back to LLMService: {}", e.getMessage());
            }
        }

        List<ChatMessage> messages = List.of(
            ChatMessage.system(systemPrompt),
            ChatMessage.user(userPrompt)
        );
        ChatRequest request = new ChatRequest();
        request.setMessages(messages);
        com.agentdesk.common.ai.model.ChatResponse response = llmService.chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
