package com.agentdesk.api.conversation.dto;

import lombok.Data;

@Data
public class MessageFeedbackRequest {

    private Long messageId;

    private Integer rating;

    private String comment;
}
