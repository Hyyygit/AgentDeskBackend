package com.agentdesk.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hyyy
 * @date 2026/5/3 21:20
 * @description
 */
@MapperScan("com.agentdesk.knowledge.mapper")
@SpringBootApplication
public class AgentDeskKnowledgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskKnowledgeApplication.class, args);
    }
}
