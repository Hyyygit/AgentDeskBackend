package com.agentdesk.orchestrator.config;

import com.agentdesk.api.event.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange agentDeskExchange() {
        return new TopicExchange(MqConstants.EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(MqConstants.QUEUE_AUDIT, true);
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue()).to(agentDeskExchange()).with("agent.run.#");
    }

    @Bean
    public Queue curationQueue() {
        return new Queue(MqConstants.QUEUE_CURATION, true);
    }

    @Bean
    public Binding curationBinding() {
        return BindingBuilder.bind(curationQueue()).to(agentDeskExchange()).with(MqConstants.KEY_TICKET_RESOLVED);
    }

    @Bean
    public Queue draftQueue() {
        return new Queue(MqConstants.QUEUE_DRAFT, true);
    }

    @Bean
    public Binding draftBinding() {
        return BindingBuilder.bind(draftQueue()).to(agentDeskExchange()).with("knowledge.draft.#");
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(MqConstants.QUEUE_NOTIFICATION, true);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(agentDeskExchange()).with("ticket.#");
    }
}
