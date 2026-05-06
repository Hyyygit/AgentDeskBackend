package com.agentdesk.conversation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hyyy
 * @date 2026/5/3 15:10
 * @description
 */
@EnableFeignClients(basePackages = "com.agentdesk")
@ComponentScan(basePackages = "com.agentdesk")
@MapperScan("com.agentdesk.conversation.mapper")
@EnableDiscoveryClient
@SpringBootApplication
public class AgentDeskConversationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskConversationApplication.class, args);
    }
}
