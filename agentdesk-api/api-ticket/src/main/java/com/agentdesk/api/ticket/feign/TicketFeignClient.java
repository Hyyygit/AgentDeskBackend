package com.agentdesk.api.ticket.feign;

import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.api.ticket.dto.TicketDTO;
import com.agentdesk.api.ticket.dto.TicketQueryDTO;
import com.agentdesk.api.ticket.dto.TicketStatusUpdateRequest;
import com.agentdesk.common.web.result.ServiceInnerResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ticket-service", path = "/internal/tickets")
public interface TicketFeignClient {

    @PostMapping("/create")
    ServiceInnerResult<TicketDTO> createTicket(@RequestBody TicketCreateRequest request);

    @GetMapping("/{ticketId}")
    TicketDTO getTicket(@PathVariable Long ticketId);

    @PutMapping("/{ticketId}/status")
    TicketDTO updateStatus(@PathVariable Long ticketId, @RequestBody TicketStatusUpdateRequest request);

    @GetMapping
    List<TicketDTO> listTickets(@SpringQueryMap TicketQueryDTO query);
}
