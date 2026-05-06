package com.agentdesk.orchestrator.agent.trace;

import com.agentdesk.api.event.AgentRunEvent;
import com.agentdesk.api.event.MqConstants;
import com.agentdesk.orchestrator.service.IAgentRunLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AgentTraceRecorder {
    private static final Logger log = LoggerFactory.getLogger(AgentTraceRecorder.class);

    @Autowired
    private IAgentRunLogService agentRunLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    public void record(String requestId, String agentName, Object output, long latencyMs) {
        record(requestId, agentName, null, output, 0.8, "PASS", (int) latencyMs);
    }

    public void record(String requestId, String agentName, Object input, Object output,
                       Double confidence, String gateResult, Integer latencyMs) {
        agentRunLogService.saveLog(requestId, agentName, input, output, confidence, gateResult, latencyMs, null);
        log.info("[{}] Agent '{}' completed in {}ms, gate: {}, confidence: {}",
                 requestId, agentName, latencyMs, gateResult, confidence);

        if (rabbitTemplate != null) {
            try {
                AgentRunEvent event = AgentRunEvent.builder()
                    .requestId(requestId)
                    .agentName(agentName)
                    .runStatus("SUCCESS")
                    .latencyMs(latencyMs)
                    .timestamp(LocalDateTime.now())
                    .build();
                rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_NAME, MqConstants.KEY_AGENT_RUN, event);
            } catch (Exception e) {
                log.warn("Failed to publish agent run event", e);
            }
        }
    }

    public void recordError(String requestId, String agentName, String errorMessage, long latencyMs) {
        agentRunLogService.saveLog(requestId, agentName, null, null, 0.0, "BLOCKED", (int) latencyMs, errorMessage);
        log.error("[{}] Agent '{}' FAILED in {}ms: {}", requestId, agentName, latencyMs, errorMessage);
    }
}
