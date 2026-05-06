package com.agentdesk.orchestrator.agent.knowledge;

import com.agentdesk.api.knowledge.dto.KnowledgeDocDTO;
import com.agentdesk.api.knowledge.dto.KnowledgeSearchRequest;
import com.agentdesk.api.knowledge.feign.KnowledgeFeignClient;
import com.agentdesk.orchestrator.agent.AbstractAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KnowledgeAgent extends AbstractAgent {

    @Autowired
    private KnowledgeFeignClient knowledgeFeignClient;

    @Override
    public String agentName() {
        return "KNOWLEDGE";
    }

    public KnowledgeResult retrieve(String userMessage, String category) {
        KnowledgeSearchRequest searchRequest = new KnowledgeSearchRequest();
        searchRequest.setKeyword(userMessage);
        searchRequest.setCategory(category);

        try {
            List<KnowledgeDocDTO> docs = knowledgeFeignClient.retrieve(searchRequest);

            KnowledgeResult result = new KnowledgeResult();
            result.setDocuments(docs);
            result.setHasAnswer(docs != null && !docs.isEmpty());
            result.setRelevanceScore(docs != null && !docs.isEmpty() ? 0.8 : 0.0);

            if (docs != null && !docs.isEmpty()) {
                String docsText = docs.stream()
                    .map(d -> d.getTitle() + ": " + d.getContent())
                    .collect(Collectors.joining("\n\n"));

                String systemPrompt = "Extract the most relevant answer from the knowledge documents for the user's question. Be concise.";
                String userPrompt = "User question: " + userMessage + "\n\nKnowledge documents:\n" + docsText + "\n\nBest answer:";
                result.setBestAnswer(callLLM(systemPrompt, userPrompt));
            }

            return result;
        } catch (Exception e) {
            log.error("Knowledge retrieval failed", e);
            KnowledgeResult fallback = new KnowledgeResult();
            fallback.setDocuments(new ArrayList<>());
            fallback.setHasAnswer(false);
            fallback.setRelevanceScore(0.0);
            return fallback;
        }
    }
}
