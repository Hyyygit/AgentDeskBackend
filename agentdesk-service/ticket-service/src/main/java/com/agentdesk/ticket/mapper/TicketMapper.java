package com.agentdesk.ticket.mapper;

import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/1 23:10
 * @description
 */
public interface TicketMapper extends BaseMapper<TicketPO> {
    List<TicketPO> selectTicketList(TicketQueryDTO ticketQueryDTO);
}
