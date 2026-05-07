package com.agentdesk.auditlog.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author hyyy
 * @date 2026/5/3 20:47
 * @description Agent运行日志持久层对象
 */
@Schema(description = "Agent运行日志持久层对象")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_run_log")
public class AgentRunLogPO extends BaseEntity {

    @TableField(exist = false)
    private Long tenantId;

    @Schema(description = "本次请求的全局ID")
    private String requestId;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "工单ID")
    private Long ticketId;

    @Schema(description = "Agent名称")
    private String agentName;

    @Schema(description = "执行状态")
    private String runStatus;

    @Schema(description = "输入参数")
    private String inputPayload;

    @Schema(description = "输出参数")
    private String outputPayload;

    @Schema(description = "置信度")
    private BigDecimal confidence;

    @Schema(description = "门控结果")
    private String gateResult;

    @Schema(description = "token消耗")
    private Integer costTokens;

    @Schema(description = "耗时毫秒")
    private Integer latencyMs;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMessage;
}
