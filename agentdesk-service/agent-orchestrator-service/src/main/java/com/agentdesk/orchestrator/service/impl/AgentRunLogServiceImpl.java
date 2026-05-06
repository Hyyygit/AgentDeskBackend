package com.agentdesk.orchestrator.service.impl;

import com.agentdesk.orchestrator.domain.AgentRunLogPO;
import com.agentdesk.orchestrator.mapper.AgentRunLogMapper;
import com.agentdesk.orchestrator.service.IAgentRunLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AgentRunLogServiceImpl extends ServiceImpl<AgentRunLogMapper, AgentRunLogPO>
        implements IAgentRunLogService {

    private static final Logger log = LoggerFactory.getLogger(AgentRunLogServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveLog(String requestId, String agentName, Object input, Object output,
                        Double confidence, String gateResult, Integer latencyMs, String errorMessage) {
        try {
            AgentRunLogPO logEntry = new AgentRunLogPO();
            logEntry.setRequestId(requestId);
            logEntry.setAgentName(agentName);
            logEntry.setRunStatus(errorMessage == null ? "SUCCESS" : "FAILED");
            logEntry.setInputPayload(objectMapper.writeValueAsString(input));
            logEntry.setOutputPayload(output instanceof String ? (String) output : objectMapper.writeValueAsString(output));
            logEntry.setConfidence(confidence != null ? BigDecimal.valueOf(confidence) : null);
            logEntry.setGateResult(gateResult);
            logEntry.setLatencyMs(latencyMs);
            logEntry.setErrorCode(errorMessage != null ? "AGENT_ERROR" : null);
            logEntry.setErrorMessage(errorMessage);
            save(logEntry);
        } catch (Exception e) {
            log.warn("Failed to save agent run log: {}", e.getMessage());
        }
    }

    @Override
    public List<AgentRunLogPO> getRequestTrace(String requestId) {
        return lambdaQuery().eq(AgentRunLogPO::getRequestId, requestId)
                .orderByAsc(AgentRunLogPO::getCreateTime).list();
    }
}
