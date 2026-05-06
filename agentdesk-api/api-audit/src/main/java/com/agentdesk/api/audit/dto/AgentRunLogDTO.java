package com.agentdesk.api.audit.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AgentRunLogDTO {
    private Long id;
    private String requestId;
    private Long conversationId;
    private Long ticketId;
    private String agentName;
    private String runStatus;
    private String inputPayload;
    private String outputPayload;
    private BigDecimal confidence;
    private String gateResult;
    private Integer costTokens;
    private Integer latencyMs;
    private Date createTime;
}
