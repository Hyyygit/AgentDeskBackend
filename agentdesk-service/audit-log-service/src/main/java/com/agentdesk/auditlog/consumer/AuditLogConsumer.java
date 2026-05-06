package com.agentdesk.auditlog.consumer;

import com.agentdesk.api.event.AgentRunEvent;
import com.agentdesk.api.event.MqConstants;
import com.agentdesk.auditlog.service.IAgentRunLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditLogConsumer {

    @Autowired
    private IAgentRunLogService agentRunLogService;

    @RabbitListener(queues = MqConstants.QUEUE_AUDIT)
    public void handleAgentRunEvent(AgentRunEvent event) {
        log.info("Received agent run event: requestId={}, agentName={}", event.getRequestId(), event.getAgentName());
        try {
            agentRunLogService.saveLog(
                event.getRequestId(),
                event.getAgentName(),
                event.getInputPayload(),
                event.getOutputPayload(),
                event.getConfidence() != null ? event.getConfidence().doubleValue() : null,
                event.getGateResult(),
                event.getLatencyMs(),
                null
            );
        } catch (Exception e) {
            log.error("Failed to handle agent run event", e);
        }
    }
}
