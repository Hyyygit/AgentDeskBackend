package com.agentdesk.ticket.controller;

import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.api.ticket.dto.TicketDTO;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.common.web.result.ServiceInnerResult;
import com.agentdesk.ticket.converter.TicketConverter;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/tickets")
@RequiredArgsConstructor
public class TicketInternalController extends BaseController {

    private final ITicketService ticketService;

    @PostMapping("/create")
    public ServiceInnerResult<TicketDTO> createTicket(@Valid @RequestBody TicketCreateRequest request,
                                                      @RequestHeader("X-User-Id") Long userId) {
        TicketPO ticket = ticketService.createTicket(request, userId);
        return ServiceInnerResult.success(TicketConverter.toDTO(ticket));
    }

    @GetMapping("/{ticketId}")
    public AjaxResult getTicket(@PathVariable Long ticketId) {
        TicketPO ticket = ticketService.getTicket(ticketId);
        if (ticket == null) {
            return error("工单不存在");
        }
        return success(TicketConverter.toVO(ticket));
    }

    @PutMapping("/{ticketId}/status")
    public AjaxResult updateStatus(@PathVariable Long ticketId,
                                   @RequestParam Integer newStatus,
                                   @RequestParam(required = false) String remark,
                                   @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        TicketPO ticket = ticketService.updateStatus(ticketId, newStatus, userId, 1, remark);
        return success(TicketConverter.toVO(ticket));
    }

    @PostMapping("/handoff")
    public AjaxResult createHumanHandoff(@RequestBody Map<String, Object> body,
                                         @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        Long ticketId = Long.valueOf(body.get("ticketId").toString());
        String summary = (String) body.getOrDefault("summary", "需要人工处理");
        String description = (String) body.getOrDefault("description", "");
        Integer priority = body.containsKey("priority") ? (Integer) body.get("priority") : 2;

        TicketCreateRequest request = new TicketCreateRequest();
        request.setSummary(summary);
        request.setDescription(description);
        request.setPriority(priority);
        request.setTicketCategory(1);

        TicketPO ticket = ticketService.createTicket(request, userId);
        ticketService.updateStatus(ticket.getId(), 5, userId, 1, "人工接手处理");

        return success(TicketConverter.toVO(ticket));
    }
}
