package com.agentdesk.orchestrator.agent.triage;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TriageAgent extends AbstractAgent {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String agentName() {
        return "TRIAGE";
    }

    public TriageResult triage(String userMessage, String conversationHistory) {
        String systemPrompt = """
            You are a Triage Agent for an enterprise work-order system. Your job is to:
            1. Classify the user's issue into one of these categories:
               ACCOUNT_ACCESS, SYSTEM_BUG, NETWORK_FAILURE, FINANCE_PROCESS,
               PERMISSION_REQUEST, COMPLAINT, CONSULTATION, GENERAL_SUPPORT
            2. Determine priority: P1 (urgent/critical), P2 (high), P3 (medium), P4 (low)
            3. Create a brief one-line summary of the issue
            4. Decide if a formal work order ticket should be created
            5. Check if the content contains sensitive information

            Return ONLY a JSON object with fields: category, priority, summary, requiresTicket (boolean), isSensitive (boolean), confidence (0.0-1.0).
            """;

        String userPrompt = "User message: " + userMessage;
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            userPrompt += "\n\nConversation history:\n" + conversationHistory;
        }

        try {
            String response = callLLM(systemPrompt, userPrompt);
            TriageResult result = mapper.readValue(response, TriageResult.class);
            return result;
        } catch (Exception e) {
            log.error("Triage failed, using fallback", e);
            TriageResult fallback = new TriageResult();
            fallback.setCategory("GENERAL_SUPPORT");
            fallback.setPriority("P3");
            fallback.setSummary(userMessage.substring(0, Math.min(100, userMessage.length())));
            fallback.setRequiresTicket(true);
            fallback.setIsSensitive(false);
            fallback.setConfidence(0.5);
            return fallback;
        }
    }
}
