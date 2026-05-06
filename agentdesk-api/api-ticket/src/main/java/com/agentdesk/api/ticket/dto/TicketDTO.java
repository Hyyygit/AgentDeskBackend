package com.agentdesk.api.ticket.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String ticketNo;

    private Long tenantId;

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

    private Date deadLine;

    private Date resolvedTime;

    private Date closedTime;

    private Date createTime;

    private Date updateTime;
}
