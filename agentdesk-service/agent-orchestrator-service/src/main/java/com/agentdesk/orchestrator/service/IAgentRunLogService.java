package com.agentdesk.orchestrator.service;

import com.agentdesk.orchestrator.domain.AgentRunLogPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IAgentRunLogService extends IService<AgentRunLogPO> {
    void saveLog(String requestId, String agentName, Object input, Object output,
                 Double confidence, String gateResult, Integer latencyMs, String errorMessage);
    List<AgentRunLogPO> getRequestTrace(String requestId);
}
