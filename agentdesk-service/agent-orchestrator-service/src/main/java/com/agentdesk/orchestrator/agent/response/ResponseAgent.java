package com.agentdesk.orchestrator.agent.response;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.agentdesk.orchestrator.agent.decision.DecisionResult;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeResult;
import com.agentdesk.orchestrator.agent.tool.ToolResult;
import org.springframework.stereotype.Component;

@Component
public class ResponseAgent extends AbstractAgent {

    @Override
    public String agentName() {
        return "RESPONSE";
    }

    public String generateResponse(DecisionResult decision, KnowledgeResult knowledge, ToolResult tool) {
        String systemPrompt = """
            You are a helpful customer service AI agent. Generate a friendly, professional response.
            - If the issue was resolved automatically, explain the solution clearly
            - If a ticket was created, provide the ticket number and next steps
            - If human handoff is needed, reassure the user
            - Keep responses concise but warm
            """;

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("Action: ").append(decision.getAction()).append("\n");
        if (knowledge.getBestAnswer() != null) {
            userPrompt.append("Knowledge answer: ").append(knowledge.getBestAnswer()).append("\n");
        }
        if (tool != null && tool.getTicketNo() != null) {
            userPrompt.append("Ticket created: ").append(tool.getTicketNo()).append("\n");
        }
        userPrompt.append("Decision reason: ").append(decision.getReason()).append("\n");
        userPrompt.append("\nGenerate a response to the user:");

        try {
            return callLLM(systemPrompt, userPrompt.toString());
        } catch (Exception e) {
            log.error("Response generation failed", e);
            if (tool != null && tool.getTicketNo() != null) {
                return "已为您创建工单 " + tool.getTicketNo() + "，我们的团队将尽快处理您的问题。";
            } else if (knowledge.getBestAnswer() != null) {
                return knowledge.getBestAnswer();
            } else {
                return "收到您的消息，我们正在为您处理。如有紧急问题，请联系人工客服。";
            }
        }
    }
}
