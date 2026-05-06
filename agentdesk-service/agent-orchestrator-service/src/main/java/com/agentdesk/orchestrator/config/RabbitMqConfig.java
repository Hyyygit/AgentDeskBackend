package com.agentdesk.orchestrator.config;

import com.agentdesk.api.event.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public TopicExchange agentDeskExchange() {
        return new TopicExchange(MqConstants.EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(MqConstants.QUEUE_AUDIT, true);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange exchange) {
        return BindingBuilder.bind(auditQueue).to(exchange).with("agent.run.#");
    }

    @Bean
    public Queue curationQueue() {
        return new Queue(MqConstants.QUEUE_CURATION, true);
    }

    @Bean
    public Binding curationBinding(Queue curationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(curationQueue).to(exchange).with("ticket.resolved");
    }

    @Bean
    public Queue draftQueue() {
        return new Queue(MqConstants.QUEUE_DRAFT, true);
    }

    @Bean
    public Binding draftBinding(Queue draftQueue, TopicExchange exchange) {
        return BindingBuilder.bind(draftQueue).to(exchange).with("knowledge.draft.#");
    }
}
