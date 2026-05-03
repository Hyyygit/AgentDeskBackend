package com.agentdesk.auditlog.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/3 20:49
 * @description 人工转接任务信息持久化对象
 */
@Schema(description = "人工转接任务信息持久化对象")
@Data
public class HumanHandoffTaskPO extends BaseEntity {

    @Schema(description = "本次请求的全局ID")
    private String requestId;

    @Schema(description = "关联工单ID")
    private Long ticketId;

    @Schema(description = "关联会话ID")
    private Long conversationId;

    @Schema(description = "转人工原因")
    private String handoffReason;

    @Schema(description = "转人工类型 LOW_CONFIDENCE/SENSITIVE/TOOL_FAILED")
    private Integer handoffType;

    @Schema(description = "指派组")
    private String assignedGroup;

    @Schema(description = "状态")
    private String status;
}
