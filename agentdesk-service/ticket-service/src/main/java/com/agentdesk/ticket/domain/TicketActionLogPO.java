package com.agentdesk.ticket.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ticket_action_log")
public class TicketActionLogPO extends BaseEntity {

    private Long ticketId;
    private Integer actionType;
    private Integer operatorType;
    private Long operatorId;
    private String actionDetail;
}
