package com.agentdesk.common.ai.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {

    private String model;

    private List<ChatMessage> messages;

    private Double temperature;

    private Integer maxTokens;

    private Boolean stream = false;
}
