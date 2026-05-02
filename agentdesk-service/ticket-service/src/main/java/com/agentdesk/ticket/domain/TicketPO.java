package com.agentdesk.ticket.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/1 23:06
 * @description 工单持久化对象
 */
@Data
@Schema(description = "工单持久化对象")
public class TicketPO extends BaseEntity {

    private String ticketNo;//工单编号
    private Integer ticketCategory;//工单类型
    private Integer priority;//工单优先级
    private Integer status;//工单状态
    private String summary;//工单摘要
    private String description;//工单详细描述
    private Integer source;//工单来源
    private String assignedGroup;//工单分配的组
    private Integer assignedUserId;//工单分配的处理人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadLine;//处理工单的预期截止时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date resolvedTime;//工单解决时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closedTime;//工单关闭时间
    private Integer comeUpUserId;//提单的用户主键ID
    private Integer conversationId;//关联的会话ID
}
