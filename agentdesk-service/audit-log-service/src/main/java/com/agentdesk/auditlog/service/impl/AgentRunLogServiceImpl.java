package com.agentdesk.auditlog.service.impl;

import com.agentdesk.auditlog.domain.AgentRunLogPO;
import com.agentdesk.auditlog.mapper.AgentRunLogMapper;
import com.agentdesk.auditlog.service.IAgentRunLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author hyyy
 * @date 2026/5/3 21:05
 * @description Agent运行日志服务实现类
 */
@Slf4j
@Service
public class AgentRunLogServiceImpl extends ServiceImpl<AgentRunLogMapper, AgentRunLogPO> implements IAgentRunLogService {

    @Override
    public void saveLog(String requestId, String agentName, String inputPayload, String outputPayload,
                        Double confidence, String gateResult, Integer latencyMs, String errorMessage) {
        try {
            AgentRunLogPO logEntry = new AgentRunLogPO();
            logEntry.setRequestId(requestId);
            logEntry.setAgentName(agentName);
            logEntry.setRunStatus(errorMessage == null ? "SUCCESS" : "FAILED");
            logEntry.setInputPayload(inputPayload);
            logEntry.setOutputPayload(outputPayload);
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
}
