package com.agentdesk.auditlog.controller;

import com.agentdesk.auditlog.domain.AgentRunLogPO;
import com.agentdesk.auditlog.service.IAgentRunLogService;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "审计控制类")
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController extends BaseController {

    private final IAgentRunLogService agentRunLogService;

    @Operation(summary = "查询Agent运行日志")
    @GetMapping("/agent-runs")
    public AjaxResult listAgentRuns(@RequestParam(required = false) String agentName,
                                    @RequestParam(required = false) String runStatus,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        startPage();
        LambdaQueryWrapper<AgentRunLogPO> wrapper = new LambdaQueryWrapper<>();
        if (agentName != null && !agentName.isEmpty()) {
            wrapper.eq(AgentRunLogPO::getAgentName, agentName);
        }
        if (runStatus != null && !runStatus.isEmpty()) {
            wrapper.eq(AgentRunLogPO::getRunStatus, runStatus);
        }
        if (startTime != null) {
            wrapper.ge(AgentRunLogPO::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AgentRunLogPO::getCreateTime, endTime);
        }
        wrapper.orderByDesc(AgentRunLogPO::getCreateTime);
        List<AgentRunLogPO> list = agentRunLogService.list(wrapper);
        return success("查询成功", TableDataInfo.getTableDataInfo(list));
    }

    @Operation(summary = "获取请求链路追踪")
    @GetMapping("/agent-runs/{requestId}")
    public AjaxResult getRequestTrace(@PathVariable String requestId) {
        LambdaQueryWrapper<AgentRunLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentRunLogPO::getRequestId, requestId)
                .orderByAsc(AgentRunLogPO::getCreateTime);
        List<AgentRunLogPO> list = agentRunLogService.list(wrapper);
        return success(list);
    }
}
