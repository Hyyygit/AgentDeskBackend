package com.agentdesk.auditlog.controller;

import com.agentdesk.auditlog.domain.TicketActionLogPO;
import com.agentdesk.auditlog.service.ITicketActionLogService;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "日志控制类")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController extends BaseController {

    private final ITicketActionLogService ticketActionLogService;

    @Operation(summary = "获取工单操作日志")
    @GetMapping("/ticket-actions/{ticketId}")
    public AjaxResult getTicketActions(@PathVariable Long ticketId) {
        List<TicketActionLogPO> list = ticketActionLogService.lambdaQuery()
                .eq(TicketActionLogPO::getTicketId, ticketId)
                .orderByAsc(TicketActionLogPO::getCreateTime)
                .list();
        return success(list);
    }
}
