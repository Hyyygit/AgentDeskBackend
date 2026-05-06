package com.agentdesk.orchestrator.service;

import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface OrchestratorService {

    OrchestrateResponse orchestrate(OrchestrateRequest request);

    SseEmitter orchestrateStream(OrchestrateRequest request);
}
