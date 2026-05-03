package com.agentdesk.auditlog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hyyy
 * @date 2026/5/3 21:21
 * @description
 */
@MapperScan("com.agentdesk.auditlog.mapper")
@SpringBootApplication
public class AgentDeskAuditLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentDeskAuditLogApplication.class, args);
    }
}
