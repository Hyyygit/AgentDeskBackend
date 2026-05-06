package com.agentdesk.auditlog.service;

import com.agentdesk.auditlog.domain.AgentRunLogPO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author hyyy
 * @date 2026/5/3 21:05
 * @description Agent运行日志服务
 */
public interface IAgentRunLogService extends IService<AgentRunLogPO> {

    void saveLog(String requestId, String agentName, String inputPayload, String outputPayload,
                 Double confidence, String gateResult, Integer latencyMs, String errorMessage);
}
