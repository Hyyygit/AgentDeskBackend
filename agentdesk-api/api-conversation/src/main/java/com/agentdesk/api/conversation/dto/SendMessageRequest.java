package com.agentdesk.api.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank
    private String content;

    private Long conversationId;

    private Integer messageType = 1;
}
