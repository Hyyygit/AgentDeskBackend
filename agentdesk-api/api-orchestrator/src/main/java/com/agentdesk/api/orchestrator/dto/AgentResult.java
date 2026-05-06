package com.agentdesk.api.orchestrator.dto;

import lombok.Data;

@Data
public class AgentResult {
    private String agentName;
    private String output;
    private Integer confidence;
    private String gateResult;
    private Long latencyMs;
}
