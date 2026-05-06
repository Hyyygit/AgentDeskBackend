package com.agentdesk.opsadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.agentdesk")
@EnableDiscoveryClient
public class OpsAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpsAdminApplication.class, args);
    }
}
