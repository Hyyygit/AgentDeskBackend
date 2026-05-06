package com.agentdesk.orchestrator.agent.tool;

import lombok.Data;

import java.io.Serializable;

@Data
public class ToolResult implements Serializable {
    private String toolName;
    private Boolean success;
    private String result;
    private Long ticketId;
    private String ticketNo;
    private String errorMessage;
}
