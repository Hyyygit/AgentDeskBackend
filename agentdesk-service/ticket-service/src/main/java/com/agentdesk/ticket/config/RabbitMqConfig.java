package com.agentdesk.ticket.config;

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
    public Queue notificationQueue() {
        return new Queue(MqConstants.QUEUE_NOTIFICATION, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with("ticket.#");
    }
}
