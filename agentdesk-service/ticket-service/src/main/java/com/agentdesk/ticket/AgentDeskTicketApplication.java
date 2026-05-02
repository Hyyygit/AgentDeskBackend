package com.agentdesk.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hyyy
 * @date 2026/5/1 22:53
 * @description 工单服务启动类
 */
@MapperScan("com.agentdesk.**.mapper")
@SpringBootApplication
public class AgentDeskTicketApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskTicketApplication.class, args);
    }
}
