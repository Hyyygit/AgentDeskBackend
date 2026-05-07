package com.agentdesk.auditlog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hyyy
 * @date 2026/5/3 21:21
 * @description
 */
@MapperScan("com.agentdesk.**.mapper")
@ComponentScan(basePackages = "com.agentdesk")
@EnableFeignClients(basePackages = "com.agentdesk")
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class AgentDeskAuditLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskAuditLogApplication.class, args);
    }
}
