package com.agentdesk.ticket.service;

import com.agentdesk.ticket.domain.TicketActionLogPO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITicketActionLogService extends IService<TicketActionLogPO> {
    void logAction(Long ticketId, Integer actionType, Integer operatorType, Long operatorId, String detail);
}
