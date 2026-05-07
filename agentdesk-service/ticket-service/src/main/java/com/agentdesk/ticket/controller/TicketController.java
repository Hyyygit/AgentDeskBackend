package com.agentdesk.ticket.controller;

import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.ticket.converter.TicketConverter;
import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;
import com.agentdesk.ticket.service.ITicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyyy
 * @date 2026/5/1 22:58
 * @description 工单管理
 */
@Tag(name = "工单管理")
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController extends BaseController {

    private final ITicketService ticketService;

    @Operation(summary = "创建工单")
    @PostMapping("/")
    public AjaxResult createTicket(@Valid @RequestBody TicketCreateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        TicketPO ticket = ticketService.createTicket(request, userId);
        return success("创建成功", TicketConverter.toVO(ticket));
    }

    @Operation(summary = "工单列表")
    @GetMapping("/")
    public AjaxResult listTickets(TicketQueryDTO query) {
        startPage();
        List<TicketVO> list = ticketService.listTickets(query).stream()
                .map(TicketConverter::toVO)
                .collect(Collectors.toList());
        return success("查询成功", TableDataInfo.getTableDataInfo(list));
    }

    @Operation(summary = "工单详情")
    @GetMapping("/{ticketId}")
    public AjaxResult getTicket(@PathVariable Long ticketId) {
        TicketPO ticket = ticketService.getTicket(ticketId);
        if (ticket == null) {
            return error("工单不存在");
        }
        return success(TicketConverter.toVO(ticket));
    }

    @Operation(summary = "更新工单状态")
    @PutMapping("/{ticketId}/status")
    public AjaxResult updateStatus(@PathVariable Long ticketId,
                                   @RequestParam Integer newStatus,
                                   @RequestParam(required = false) String remark) {
        Long userId = UserContext.getCurrentUserId();
        TicketPO ticket = ticketService.updateStatus(ticketId, newStatus, userId, 2, remark);
        return success("状态更新成功", TicketConverter.toVO(ticket));
    }

    @Operation(summary = "分配工单")
    @PutMapping("/{ticketId}/assign")
    public AjaxResult assignTicket(@PathVariable Long ticketId,
                                   @RequestParam(required = false) Long assignedUserId,
                                   @RequestParam(required = false) String assignedGroup) {
        Long userId = UserContext.getCurrentUserId();
        TicketPO ticket = ticketService.assignTicket(ticketId, assignedUserId, assignedGroup, userId);
        return success("分配成功", TicketConverter.toVO(ticket));
    }

    @Operation(summary = "更新优先级")
    @PutMapping("/{ticketId}/priority")
    public AjaxResult updatePriority(@PathVariable Long ticketId,
                                     @RequestParam Integer priority) {
        Long userId = UserContext.getCurrentUserId();
        TicketPO ticket = ticketService.updatePriority(ticketId, priority, userId);
        return success("优先级更新成功", TicketConverter.toVO(ticket));
    }

    @Operation(summary = "添加评论")
    @PostMapping("/{ticketId}/comments")
    public AjaxResult addComment(@PathVariable Long ticketId,
                                 @RequestBody Map<String, String> body) {
        Long userId = UserContext.getCurrentUserId();
        String comment = body.get("comment");
        ticketService.addComment(ticketId, comment, userId, 2);
        return success("评论成功");
    }

    @Operation(summary = "工单时间线")
    @GetMapping("/{ticketId}/timeline")
    public AjaxResult getTimeline(@PathVariable Long ticketId) {
        return success(ticketService.getTimeline(ticketId));
    }

    @Operation(summary = "工单统计")
    @GetMapping("/stats")
    public AjaxResult getStats() {
        Long userId = UserContext.getCurrentUserId();
        return success(ticketService.getStats(userId));
    }
}
