package com.agentdesk.orchestrator.service.impl;

import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.orchestrator.agent.coordinator.CoordinatorAgent;
import com.agentdesk.orchestrator.agent.trace.AgentTraceRecorder;
import com.agentdesk.orchestrator.service.OrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrchestratorServiceImpl implements OrchestratorService {
    private static final Logger log = LoggerFactory.getLogger(OrchestratorServiceImpl.class);

    @Autowired
    private CoordinatorAgent coordinatorAgent;

    @Autowired
    private AgentTraceRecorder traceRecorder;

    @Override
    public OrchestrateResponse orchestrate(OrchestrateRequest request) {
        log.info("Orchestrating request: {}", request.getRequestId());
        long startTime = System.currentTimeMillis();

        try {
            OrchestrateResponse response = coordinatorAgent.orchestrate(request);
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Orchestration completed in {}ms, ticket={}, needHuman={}",
                    elapsed, response.getTicketNo(), response.getNeedHuman());
            return response;
        } catch (Exception e) {
            log.error("Orchestration failed", e);
            traceRecorder.recordError(request.getRequestId(), "COORDINATOR", e.getMessage(),
                    System.currentTimeMillis() - startTime);

            OrchestrateResponse fallback = new OrchestrateResponse();
            fallback.setRequestId(request.getRequestId());
            fallback.setReplyContent("抱歉，系统处理您的请求时遇到了问题。已转接人工客服，请稍候。");
            fallback.setNeedHuman(true);
            fallback.setConfidence(0);
            return fallback;
        }
    }

    @Override
    public SseEmitter orchestrateStream(OrchestrateRequest request) {
        SseEmitter emitter = new SseEmitter(300000L);

        AuthUser authUser = UserContext.getCurrentUser();

        executorService.submit(() -> {
            try {
                if (authUser != null){
                    UserContext.setCurrentUser(authUser);
                }

                coordinatorAgent.orchestrateStream(request, chunk -> {
                    try {
                        String trimmed = chunk.trim();
                        if ("[DONE]".equals(trimmed)) {
                            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                        } else {
                            emitter.send(SseEmitter.event().data(chunk));
                        }
                    } catch (IOException e) {
                        log.error("SSE send failed", e);
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                log.error("SSE orchestrator stream failed", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ignored) {
                }
                emitter.completeWithError(e);
            } finally {
                UserContext.clear();
            }
        });

        return emitter;
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();
}
