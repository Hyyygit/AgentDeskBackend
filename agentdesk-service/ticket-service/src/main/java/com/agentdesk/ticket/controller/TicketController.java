package com.agentdesk.ticket.controller;

import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.service.ITicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyyy
 * @date 2026/5/1 22:58
 * @description 工单管理
 */
@Tag(name = "工单管理")
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController extends BaseController {

    private final ITicketService ticketService;//注入工单服务

    @Operation(summary = "工单列表")
    @GetMapping("/list")
    public AjaxResult getTicketList(TicketQueryDTO ticketQueryDTO) {//得到工单列表
        startPage();//开启分页查询功能
        TableDataInfo pageInfo = TableDataInfo.getTableDataInfo(ticketService.getTicketList(ticketQueryDTO));//将得到的分页数据封装起来，包含总条数和数据
        return success("查询成功", pageInfo);
    }
}
