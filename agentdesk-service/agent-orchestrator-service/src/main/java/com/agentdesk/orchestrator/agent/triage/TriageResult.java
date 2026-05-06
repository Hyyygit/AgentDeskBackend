package com.agentdesk.orchestrator.agent.triage;

import lombok.Data;

import java.io.Serializable;

@Data
public class TriageResult implements Serializable {
    private String category;
    private String priority;
    private String summary;
    private Boolean requiresTicket;
    private Boolean isSensitive;
    private Double confidence;
}
