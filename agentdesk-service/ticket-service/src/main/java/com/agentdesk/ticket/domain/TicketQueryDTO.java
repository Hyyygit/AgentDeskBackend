package com.agentdesk.ticket.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/2 13:51
 * @description 前端传过来的工单数据
 */
@Data
@Schema(description = "前端传递的工单数据")
public class TicketQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工单类型")
    private Integer ticketCategory;//工单类型
    @Schema(description = "工单优先级")
    private Integer priority;//工单优先级
    @Schema(description = "工单状态")
    private Integer status;//工单状态
    @Schema(description = "工单来源")
    private Integer source;//工单来源
    @Schema(description = "工单分配的组")
    private String assignedGroup;//工单分配的组
    @Schema(description = "工单分配的处理人ID")
    private Integer assignedUserId;//工单分配的处理人ID
    @Schema(description = "处理工单的预期截止时间")
    private Date deadLine;//处理工单的预期截止时间
    @Schema(description = "工单解决时间")
    private Date resolvedTime;//工单解决时间
    @Schema(description = "工单关闭时间")
    private Date closedTime;//工单关闭时间
    @Schema(description = "提单的用户主键ID")
    private Integer userId;//提单的用户主键ID
    @Schema(description = "工单类型")
    private Integer conversationId;//关联的会话ID
    @Schema(description = "工单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Schema(description = "工单更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
