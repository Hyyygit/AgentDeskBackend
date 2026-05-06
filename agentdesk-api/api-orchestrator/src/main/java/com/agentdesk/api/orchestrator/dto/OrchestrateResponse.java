package com.agentdesk.api.orchestrator.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrchestrateResponse {
    private String requestId;
    private String replyContent;
    private Long ticketId;
    private String ticketNo;
    private Integer confidence;
    private Boolean needHuman;
    private List<AgentResult> agentResults;
}
