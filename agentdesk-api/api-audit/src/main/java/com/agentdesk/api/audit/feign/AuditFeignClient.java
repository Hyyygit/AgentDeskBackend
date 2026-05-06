package com.agentdesk.api.audit.feign;

import com.agentdesk.api.audit.dto.AgentRunLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-log-service", path = "/internal/audit")
public interface AuditFeignClient {

    @PostMapping("/agent-runs")
    void saveAgentRunLog(@RequestBody AgentRunLogDTO dto);
}
