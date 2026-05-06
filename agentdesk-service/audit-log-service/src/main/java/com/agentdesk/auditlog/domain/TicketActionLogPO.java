package com.agentdesk.auditlog.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hyyy
 * @date 2026/5/3 20:50
 * @description 工单操作日志持久化对象
 */
@Schema(description = "工单操作日志持久层对象")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket_action_log")
public class TicketActionLogPO extends BaseEntity {

    @Schema(description = "工单ID")
    private Long ticketId;

    @Schema(description = "动作类型")
    private Integer actionType;

    @Schema(description = "操作者类型 USER/AGENT/HUMAN/SYSTEM")
    private Integer operatorType;

    @Schema(description = "操作者ID")
    private Long operatorId;

    @Schema(description = "动作详情")
    private String actionDetail;
}
