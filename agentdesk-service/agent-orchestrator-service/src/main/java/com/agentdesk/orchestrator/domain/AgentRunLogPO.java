package com.agentdesk.orchestrator.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_run_log")
public class AgentRunLogPO extends BaseEntity {
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
    private String errorCode;
    private String errorMessage;
}
