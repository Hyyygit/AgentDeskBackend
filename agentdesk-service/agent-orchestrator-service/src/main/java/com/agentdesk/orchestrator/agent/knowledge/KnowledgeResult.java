package com.agentdesk.orchestrator.agent.knowledge;

import com.agentdesk.api.knowledge.dto.KnowledgeDocDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KnowledgeResult implements Serializable {
    private List<KnowledgeDocDTO> documents;
    private String bestAnswer;
    private Double relevanceScore;
    private Boolean hasAnswer;
}
