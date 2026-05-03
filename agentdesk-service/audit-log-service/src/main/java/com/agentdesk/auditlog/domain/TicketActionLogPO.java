package com.agentdesk.auditlog.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author hyyy
 * @date 2026/5/3 20:50
 * @description 工单操作日志持久化对象
 */
@Schema(description = "工单操作日志持久层对象")
@Data
public class TicketActionLogPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

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

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
