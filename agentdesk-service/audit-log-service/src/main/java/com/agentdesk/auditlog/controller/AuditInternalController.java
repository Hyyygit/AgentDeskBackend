package com.agentdesk.auditlog.controller;

import com.agentdesk.auditlog.domain.AgentRunLogPO;
import com.agentdesk.auditlog.service.IAgentRunLogService;
import com.agentdesk.api.audit.dto.AgentRunLogDTO;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/audit")
@RequiredArgsConstructor
public class AuditInternalController extends BaseController {

    private final IAgentRunLogService agentRunLogService;

    @Operation(summary = "保存Agent运行日志(内部调用)")
    @PostMapping("/agent-runs")
    public AjaxResult saveAgentRunLog(@RequestBody AgentRunLogDTO dto) {
        AgentRunLogPO po = new AgentRunLogPO();
        po.setRequestId(dto.getRequestId());
        po.setConversationId(dto.getConversationId());
        po.setTicketId(dto.getTicketId());
        po.setAgentName(dto.getAgentName());
        po.setRunStatus(dto.getRunStatus());
        po.setInputPayload(dto.getInputPayload());
        po.setOutputPayload(dto.getOutputPayload());
        po.setConfidence(dto.getConfidence());
        po.setGateResult(dto.getGateResult());
        po.setCostTokens(dto.getCostTokens());
        po.setLatencyMs(dto.getLatencyMs());
        agentRunLogService.save(po);
        return success("保存成功");
    }
}
