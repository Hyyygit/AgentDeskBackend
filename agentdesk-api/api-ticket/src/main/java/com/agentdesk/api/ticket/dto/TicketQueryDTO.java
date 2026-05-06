package com.agentdesk.api.ticket.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TicketQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ticketNo;

    private Long userId;

    private Long conversationId;

    private Integer ticketCategory;

    private Integer priority;

    private Integer status;

    private String assignedGroup;

    private Long assignedUserId;

    private Date createTimeStart;

    private Date createTimeEnd;

    private Integer pageNum;

    private Integer pageSize;
}
