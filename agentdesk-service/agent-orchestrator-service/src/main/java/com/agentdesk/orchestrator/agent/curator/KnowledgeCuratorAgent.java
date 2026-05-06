package com.agentdesk.orchestrator.agent.curator;

import com.agentdesk.api.knowledge.dto.KnowledgeDraftDTO;
import com.agentdesk.api.knowledge.feign.KnowledgeFeignClient;
import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeCuratorAgent extends AbstractAgent {

    @Autowired
    private KnowledgeFeignClient knowledgeFeignClient;

    @Override
    public String agentName() {
        return "CURATOR";
    }

    public void generateDraft(Long ticketId, String ticketSummary, String ticketDescription, String resolution) {
        String systemPrompt = """
            You are a Knowledge Curator Agent. Based on the resolved ticket, generate a knowledge base draft.
            Create a title, category, and detailed content for future reference.
            Return JSON: {"title": "...", "category": "...", "tags": "...", "content": "...", "confidence": 0.0}
            """;

        String userPrompt = "Ticket summary: " + ticketSummary + "\n"
            + "Description: " + ticketDescription + "\n"
            + "Resolution: " + resolution;

        try {
            String response = callLLM(systemPrompt, userPrompt);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            KnowledgeDraftDTO draft = new KnowledgeDraftDTO();
            draft.setSourceTicketId(ticketId);
            draft.setTitle(json.get("title").asText());
            draft.setCategory(json.get("category").asText());
            draft.setTags(json.get("tags").asText());
            draft.setContent(json.get("content").asText());
            draft.setConfidence(json.get("confidence").decimalValue());
            draft.setReviewStatus("PENDING");

            log.info("Generated knowledge draft for ticket {}: {}", ticketId, json.get("title").asText());
        } catch (Exception e) {
            log.error("Curator failed for ticket {}", ticketId, e);
        }
    }
}
