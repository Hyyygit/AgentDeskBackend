package com.agentdesk.api.orchestrator.feign;

import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "agent-orchestrator-service", path = "/internal/agent")
public interface OrchestratorFeignClient {

    @PostMapping("/orchestrate")
    OrchestrateResponse orchestrate(@RequestBody OrchestrateRequest request);
}
