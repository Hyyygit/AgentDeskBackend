package com.agentdesk.ticket.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/2 19:16
 * @description 传给前端的工单数据
 */
@Data
public class TicketVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private String ticketNo;
    private Long userId;
    private Long conversationId;
    private Integer ticketCategory;
    private Integer priority;
    private Integer status;
    private Integer source;
    private String summary;
    private String description;
    private String assignedGroup;
    private Long assignedUserId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadLine;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date resolvedTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closedTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
