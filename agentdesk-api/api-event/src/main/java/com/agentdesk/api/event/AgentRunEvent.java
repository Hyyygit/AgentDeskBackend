package com.agentdesk.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRunEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private Long conversationId;
    private Long ticketId;
    private String agentName;
    private String runStatus;
    private String inputPayload;
    private String outputPayload;
    private BigDecimal confidence;
    private String gateResult;
    private Integer latencyMs;
    private Integer costTokens;
    private String errorMessage;
    private LocalDateTime timestamp;
}
