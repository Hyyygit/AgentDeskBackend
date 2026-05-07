package com.agentdesk.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hyyy
 * @date 2026/5/3 21:20
 * @description
 */
@MapperScan("com.agentdesk.knowledge.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.agentdesk")
@ComponentScan(basePackages = "com.agentdesk")
@EnableRabbit
@EnableDiscoveryClient
public class AgentDeskKnowledgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskKnowledgeApplication.class, args);
    }
}
