package com.agentdesk.orchestrator.agent.curator;

import com.agentdesk.api.event.KnowledgeDraftEvent;
import com.agentdesk.api.event.MqConstants;
import com.agentdesk.api.event.TicketEvent;
import com.agentdesk.api.knowledge.feign.KnowledgeFeignClient;
import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeCuratorAgent extends AbstractAgent {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeCuratorAgent.class);

    @Autowired
    private KnowledgeFeignClient knowledgeFeignClient;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Override
    public String agentName() {
        return "CURATOR";
    }

    @RabbitListener(queues = MqConstants.QUEUE_CURATION)
    public void onTicketResolved(TicketEvent event) {
        log.info("Curator received ticket resolved event: ticketNo={}, ticketId={}",
                 event.getTicketNo(), event.getTicketId());
        try {
            if (event.getTicketId() == null) {
                log.warn("Curator: ticketId is null, skipping draft generation");
                return;
            }
            String resolution = "Ticket " + event.getTicketNo() + " resolved. Summary: " + event.getSummary();
            generateDraft(event.getTicketId(), event.getSummary(), resolution, resolution);
        } catch (Exception e) {
            log.error("Curator failed to process ticket resolved event: {}", event.getTicketNo(), e);
        }
    }

    public void generateDraft(Long ticketId, String ticketSummary, String ticketDescription, String resolution) {
        String systemPrompt = """
            你是一个知识沉淀Agent。根据已解决的工单，生成一篇知识库草稿。
            创建标题、分类和详细内容，供将来参考。
            返回JSON: {"title": "...", "category": "...", "tags": "...", "content": "...", "confidence": 0.0}
            """;

        String userPrompt = "工单摘要: " + ticketSummary + "\n"
            + "问题描述: " + ticketDescription + "\n"
            + "解决方案: " + resolution;

        try {
            String response = callLLM(systemPrompt, userPrompt);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            String title = json.get("title").asText();
            String category = json.get("category").asText();
            String tags = json.get("tags").asText();
            String content = json.get("content").asText();

            log.info("Generated knowledge draft for ticket {}: {}", ticketId, title);

            if (rabbitTemplate != null) {
                KnowledgeDraftEvent draftEvent = KnowledgeDraftEvent.builder()
                    .sourceTicketId(ticketId)
                    .title(title)
                    .category(category)
                    .content(content)
                    .tags(tags)
                    .confidence(json.get("confidence").decimalValue())
                    .build();
                rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_NAME, MqConstants.KEY_KNOWLEDGE_DRAFT, draftEvent);
                log.info("Published knowledge draft event for ticket {}", ticketId);
            } else {
                log.warn("RabbitTemplate not available, draft for ticket {} not published", ticketId);
            }
        } catch (Exception e) {
            log.error("Curator failed for ticket {}", ticketId, e);
        }
    }
}
