package com.agentdesk.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hyyy
 * @date 2026/5/1 22:53
 * @description 工单服务启动类
 */
@MapperScan("com.agentdesk.**.mapper")
@ComponentScan(basePackages = "com.agentdesk")
@EnableFeignClients(basePackages = "com.agentdesk")
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class AgentDeskTicketApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskTicketApplication.class, args);
    }
}
