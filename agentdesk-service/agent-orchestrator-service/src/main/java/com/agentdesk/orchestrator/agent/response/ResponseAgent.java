package com.agentdesk.orchestrator.agent.response;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.agentdesk.orchestrator.agent.decision.DecisionResult;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeResult;
import com.agentdesk.orchestrator.agent.tool.ToolResult;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

@Component
public class ResponseAgent extends AbstractAgent {

    @Override
    public String agentName() {
        return "RESPONSE";
    }

    public String generateResponse(DecisionResult decision, KnowledgeResult knowledge, ToolResult tool) {
        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(decision, knowledge, tool);

        try {
            return callLLM(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("Response generation failed", e);
            return fallbackResponse(tool, knowledge);
        }
    }

    public void generateResponseStream(DecisionResult decision, KnowledgeResult knowledge, ToolResult tool,
                                       Consumer<String> chunkConsumer) {
        if (chatModel == null) {
            String full = generateResponse(decision, knowledge, tool);
            chunkConsumer.accept(full);
            return;
        }

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(decision, knowledge, tool);

        try {
            Message systemMsg = new SystemMessage(systemPrompt);
            Message userMsg = new UserMessage(userPrompt);
            Prompt prompt = new Prompt(List.of(systemMsg, userMsg));
            Flux<ChatResponse> flux = chatModel.stream(prompt);
            flux.doOnNext(chunk -> {
                if (chunk.getResult() != null && chunk.getResult().getOutput() != null) {
                    String text = chunk.getResult().getOutput().getText();
                    if (text != null) {
                        chunkConsumer.accept(text);
                    }
                }
            }).blockLast();
        } catch (Exception e) {
            log.error("Streaming response generation failed", e);
            String fallback = fallbackResponse(tool, knowledge);
            chunkConsumer.accept(fallback);
        }
    }

    private String buildSystemPrompt() {
        return """
            你是一个专业的客服AI助手。请生成友好、专业的回复。
            - 如果问题已自动解决，请清晰地解释解决方案
            - 如果已创建工单，请提供工单编号并说明后续步骤
            - 如果需要转人工，请安抚用户情绪
            - 回复要简洁但温暖
            - 用中文回复
            """;
    }

    private String buildUserPrompt(DecisionResult decision, KnowledgeResult knowledge, ToolResult tool) {
        StringBuilder sb = new StringBuilder();
        sb.append("处理动作: ").append(decision.getAction()).append("\n");
        if (knowledge.getBestAnswer() != null) {
            sb.append("知识库答案: ").append(knowledge.getBestAnswer()).append("\n");
        }
        if (tool != null && tool.getTicketNo() != null) {
            sb.append("已创建工单: ").append(tool.getTicketNo()).append("\n");
        }
        sb.append("决策理由: ").append(decision.getReason()).append("\n");
        sb.append("\n请生成给用户的回复:");
        return sb.toString();
    }

    private String fallbackResponse(ToolResult tool, KnowledgeResult knowledge) {
        if (tool != null && tool.getTicketNo() != null) {
            return "已为您创建工单 " + tool.getTicketNo() + "，我们的团队将尽快处理您的问题。";
        } else if (knowledge != null && knowledge.getBestAnswer() != null) {
            return knowledge.getBestAnswer();
        } else {
            return "收到您的消息，我们正在为您处理。如有紧急问题，请联系人工客服。";
        }
    }
}
