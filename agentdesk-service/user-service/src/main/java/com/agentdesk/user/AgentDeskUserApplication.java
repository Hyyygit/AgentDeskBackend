package com.agentdesk.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hyyy
 * @date 2026/5/2 15:10
 * @description
 */
@MapperScan("com.agentdesk.user.mapper")
@EnableFeignClients(basePackages = "com.agentdesk")
@ComponentScan(basePackages = "com.agentdesk")
@SpringBootApplication
public class AgentDeskUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskUserApplication.class, args);
    }
}
