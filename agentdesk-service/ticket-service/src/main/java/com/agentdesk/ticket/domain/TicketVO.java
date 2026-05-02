package com.agentdesk.ticket.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/2 19:16
 * @description
 */
@Data
public class TicketVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;//工单主键ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//工单创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//工单更新时间

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
