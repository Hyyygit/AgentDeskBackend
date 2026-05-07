package com.agentdesk.orchestrator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.agentdesk.orchestrator.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.agentdesk")
@ComponentScan(basePackages = "com.agentdesk")
@EnableRabbit
@EnableDiscoveryClient
public class OrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }
}
