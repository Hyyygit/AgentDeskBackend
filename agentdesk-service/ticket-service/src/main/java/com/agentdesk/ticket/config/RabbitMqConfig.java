package com.agentdesk.ticket.config;

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
    public Queue notificationQueue() {
        return new Queue(MqConstants.QUEUE_NOTIFICATION, true);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(agentDeskExchange()).with("ticket.#");
    }
}
