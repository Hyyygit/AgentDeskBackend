package com.agentdesk.orchestrator.agent.decision;

import lombok.Data;

import java.io.Serializable;

@Data
public class DecisionResult implements Serializable {
    private String action;
    private String reason;
    private String suggestedResponse;
    private Double confidence;
}
