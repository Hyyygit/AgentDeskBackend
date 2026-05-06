package com.agentdesk.api.conversation.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ConversationMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long conversationId;

    private String requestId;

    private Long senderId;

    private Integer senderType;

    private Integer messageType;

    private String content;

    private String extraJson;

    private Date createTime;
}
