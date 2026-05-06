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

    private Integer pageNum;
    private Integer pageSize;

    private String ticketNo;
    private Integer ticketCategory;
    private Integer priority;
    private Integer status;
    private Integer source;
    private String assignedGroup;
    private Long assignedUserId;
    private Date deadLine;
    private Date resolvedTime;
    private Date closedTime;
    private Long userId;
    private Long conversationId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;
}
