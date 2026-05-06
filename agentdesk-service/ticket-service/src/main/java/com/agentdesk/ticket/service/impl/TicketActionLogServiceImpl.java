package com.agentdesk.ticket.service.impl;

import com.agentdesk.ticket.domain.TicketActionLogPO;
import com.agentdesk.ticket.mapper.TicketActionLogMapper;
import com.agentdesk.ticket.service.ITicketActionLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TicketActionLogServiceImpl extends ServiceImpl<TicketActionLogMapper, TicketActionLogPO> implements ITicketActionLogService {

    @Override
    public void logAction(Long ticketId, Integer actionType, Integer operatorType, Long operatorId, String detail) {
        TicketActionLogPO log = new TicketActionLogPO();
        log.setTicketId(ticketId);
        log.setActionType(actionType);
        log.setOperatorType(operatorType);
        log.setOperatorId(operatorId);
        log.setActionDetail(detail);
        save(log);
    }
}
