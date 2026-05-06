package com.agentdesk.ticket.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("ticket")
public class TicketPO extends BaseEntity {

    private String ticketNo;
    private Integer ticketCategory;
    private Integer priority;
    private Integer status;
    private String summary;
    private String description;
    private Integer source;
    private String assignedGroup;
    private Long assignedUserId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadLine;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date resolvedTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closedTime;
    private Long userId;
    private Long conversationId;
}
