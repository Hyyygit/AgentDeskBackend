package com.agentdesk.orchestrator.controller;

import com.agentdesk.orchestrator.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/agent")
public class OrchestratorController {

    @Autowired
    private OrchestratorService orchestratorService;

    @PostMapping("/orchestrate")
    public OrchestrateResponse orchestrate(@RequestBody OrchestrateRequest request) {
        return orchestratorService.orchestrate(request);
    }

    @PostMapping("/orchestrate/stream")
    public SseEmitter orchestrateStream(@RequestBody OrchestrateRequest request) {
        return orchestratorService.orchestrateStream(request);
    }
}
