package com.agentdesk.api.orchestrator.dto;

import lombok.Data;

@Data
public class OrchestrateRequest {
    private String content;
    private Long conversationId;
    private Long userId;
    private String requestId;
}
