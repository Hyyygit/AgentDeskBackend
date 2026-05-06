package com.agentdesk.opsadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.agentdesk")
public class OpsAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpsAdminApplication.class, args);
    }
}
