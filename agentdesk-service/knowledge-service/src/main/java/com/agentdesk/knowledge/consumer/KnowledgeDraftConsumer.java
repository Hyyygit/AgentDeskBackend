package com.agentdesk.knowledge.consumer;

import com.agentdesk.api.event.KnowledgeDraftEvent;
import com.agentdesk.api.event.MqConstants;
import com.agentdesk.knowledge.domain.KnowledgeDraftPO;
import com.agentdesk.knowledge.service.IKnowledgeDraftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KnowledgeDraftConsumer {

    @Autowired
    private IKnowledgeDraftService knowledgeDraftService;

    @RabbitListener(queues = MqConstants.QUEUE_DRAFT)
    public void handleDraftEvent(KnowledgeDraftEvent event) {
        log.info("Received knowledge draft event: sourceTicketId={}, title={}", event.getSourceTicketId(), event.getTitle());
        try {
            KnowledgeDraftPO draft = new KnowledgeDraftPO();
            draft.setSourceTicketId(event.getSourceTicketId());
            draft.setTitle(event.getTitle());
            draft.setCategory(event.getCategory());
            draft.setContent(event.getContent());
            draft.setTags(event.getTags());
            draft.setConfidence(event.getConfidence());
            draft.setReviewStatus("PENDING");
            knowledgeDraftService.createDraft(draft);
        } catch (Exception e) {
            log.error("Failed to handle knowledge draft event", e);
        }
    }
}
