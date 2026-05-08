package com.agentdesk.orchestrator.controller;

import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.orchestrator.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    public SseEmitter orchestrateStream(@RequestBody OrchestrateRequest request, @RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestHeader(value = "X-Tenant-Id", required = false) Long tenantId) {
        if (userId != null && UserContext.getCurrentUserId() == null) {
            AuthUser authUser = new AuthUser();
            authUser.setUserId(userId);
            authUser.setTenantId(tenantId);
            UserContext.setCurrentUser(authUser);
        }

        try {
            return orchestratorService.orchestrateStream(request);
        } finally {
            if (userId != null && UserContext.getCurrentUser() != null
                    && UserContext.getCurrentUser().getUserId().equals(userId)) {
                UserContext.clear();
            }
        }
    }
}
