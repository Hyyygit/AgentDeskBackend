package com.agentdesk.orchestrator.agent.decision;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeResult;
import com.agentdesk.orchestrator.agent.triage.TriageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DecisionAgent extends AbstractAgent {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String agentName() {
        return "DECISION";
    }

    public DecisionResult decide(TriageResult triageResult, KnowledgeResult knowledgeResult, String userMessage) {
        if (triageResult.getIsSensitive() != null && triageResult.getIsSensitive()) {
            DecisionResult result = new DecisionResult();
            result.setAction("HUMAN_HANDOFF");
            result.setReason("Sensitive content detected");
            result.setConfidence(1.0);
            return result;
        }

        if (triageResult.getConfidence() != null && triageResult.getConfidence() < 0.6) {
            DecisionResult result = new DecisionResult();
            result.setAction("HUMAN_HANDOFF");
            result.setReason("Low triage confidence: " + triageResult.getConfidence());
            result.setConfidence(triageResult.getConfidence());
            return result;
        }

        if (knowledgeResult.getHasAnswer() && knowledgeResult.getRelevanceScore() > 0.7) {
            DecisionResult result = new DecisionResult();
            result.setAction("AUTO_RESOLVE");
            result.setReason("Knowledge base has relevant answer");
            result.setSuggestedResponse(knowledgeResult.getBestAnswer());
            result.setConfidence(knowledgeResult.getRelevanceScore());
            return result;
        }

        String systemPrompt = """
            You are a Decision Agent. Based on the triage and knowledge results, decide the action:
            - AUTO_RESOLVE: If knowledge base has a good answer
            - CREATE_TICKET: If a formal ticket is needed
            - HUMAN_HANDOFF: If the issue requires human intervention

            Return ONLY JSON: {"action": "...", "reason": "...", "suggestedResponse": "...", "confidence": 0.0}
            """;

        try {
            String context = "Triage: " + triageResult.getCategory() + " " + triageResult.getPriority() + "\n"
                + "Knowledge: " + (knowledgeResult.getHasAnswer() ? "Found relevant docs" : "No relevant docs");
            String response = callLLM(systemPrompt, context);
            return mapper.readValue(response, DecisionResult.class);
        } catch (Exception e) {
            log.error("Decision failed, using fallback", e);
            DecisionResult fallback = new DecisionResult();
            fallback.setAction("CREATE_TICKET");
            fallback.setReason("Fallback decision due to error: " + e.getMessage());
            fallback.setConfidence(0.5);
            return fallback;
        }
    }
}
