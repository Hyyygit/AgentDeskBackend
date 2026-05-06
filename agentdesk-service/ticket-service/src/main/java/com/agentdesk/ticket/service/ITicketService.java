package com.agentdesk.ticket.service;

import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.ticket.domain.TicketActionLogPO;
import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author hyyy
 * @date 2026/5/1 23:04
 * @description 工单数据服务层
 */
public interface ITicketService extends IService<TicketPO> {
    TicketPO createTicket(TicketCreateRequest request, Long userId);

    TicketPO getTicket(Long ticketId);

    TicketPO getTicketByNo(String ticketNo);

    List<TicketPO> listTickets(TicketQueryDTO query);

    List<TicketPO> listUserTickets(Long userId);

    TicketPO updateStatus(Long ticketId, Integer newStatus, Long operatorId, Integer operatorType, String remark);

    TicketPO assignTicket(Long ticketId, Long assignedUserId, String assignedGroup, Long operatorId);

    TicketPO updatePriority(Long ticketId, Integer priority, Long operatorId);

    void addComment(Long ticketId, String comment, Long operatorId, Integer operatorType);

    List<TicketActionLogPO> getTimeline(Long ticketId);

    Map<String, Long> getStats(Long userId);
}
