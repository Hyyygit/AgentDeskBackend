package com.agentdesk.orchestrator.controller;

import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.orchestrator.domain.AgentRunLogPO;
import com.agentdesk.orchestrator.service.IAgentRunLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orchestrator")
public class AgentRunLogController extends BaseController {

    @Autowired
    private IAgentRunLogService agentRunLogService;

    @GetMapping("/runs/{requestId}")
    public AjaxResult getRequestTrace(@PathVariable String requestId) {
        List<AgentRunLogPO> logs = agentRunLogService.getRequestTrace(requestId);
        return success(logs);
    }

    @GetMapping("/runs")
    public AjaxResult listRuns(@RequestParam(required = false) String agentName,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "20") Integer pageSize) {
        LambdaQueryWrapper<AgentRunLogPO> wrapper = new LambdaQueryWrapper<>();
        if (agentName != null && !agentName.isEmpty()) {
            wrapper.eq(AgentRunLogPO::getAgentName, agentName);
        }
        wrapper.orderByDesc(AgentRunLogPO::getCreateTime);
        startPage();
        List<AgentRunLogPO> list = agentRunLogService.list(wrapper);
        return success(TableDataInfo.getTableDataInfo(list));
    }
}
