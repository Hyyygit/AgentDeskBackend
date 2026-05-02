package com.agentdesk.ticket.service;

import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/1 23:04
 * @description 工单数据服务层
 */
public interface ITicketService extends IService<TicketPO> {
    List<TicketVO> getTicketList(TicketQueryDTO ticketQueryDTO);
}
