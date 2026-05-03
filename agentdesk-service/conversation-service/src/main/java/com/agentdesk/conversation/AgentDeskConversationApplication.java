package com.agentdesk.conversation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hyyy
 * @date 2026/5/3 15:10
 * @description
 */
@MapperScan("com.agentdesk.conversation.mapper")
@SpringBootApplication
public class AgentDeskConversationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskConversationApplication.class, args);
    }
}
