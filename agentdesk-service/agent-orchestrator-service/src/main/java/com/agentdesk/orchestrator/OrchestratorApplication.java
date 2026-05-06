package com.agentdesk.orchestrator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.agentdesk.orchestrator.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.agentdesk")
@ComponentScan(basePackages = "com.agentdesk")
public class OrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }
}
