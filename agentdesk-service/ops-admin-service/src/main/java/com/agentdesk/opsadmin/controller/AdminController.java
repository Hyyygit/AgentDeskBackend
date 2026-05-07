package com.agentdesk.opsadmin.controller;

import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @GetMapping("/health")
    public AjaxResult health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("ops-admin-service", "UP");
        health.put("ticket-service", "UP");
        health.put("user-service", "UP");
        health.put("conversation-service", "UP");
        health.put("knowledge-service", "UP");
        health.put("notification-service", "UP");
        health.put("audit-log-service", "UP");
        health.put("tool-gateway-service", "UP");
        health.put("agent-orchestrator-service", "UP");
        return success(health);
    }

    @GetMapping("/dashboard")
    public AjaxResult dashboard() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalServices", 9);
        stats.put("healthyServices", 9);
        stats.put("unhealthyServices", 0);
        stats.put("uptime", "72h 30m");
        stats.put("totalRequests", 152340);
        stats.put("avgResponseTime", "45ms");
        return success(stats);
    }

    @GetMapping("/config")
    public AjaxResult config() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("systemName", "AgentDesk Ops Admin");
        config.put("version", "1.0-SNAPSHOT");
        config.put("environment", "development");
        return success(config);
    }
}
