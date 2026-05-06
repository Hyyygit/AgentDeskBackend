package com.agentdesk.api.conversation.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ConversationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String conversationNo;

    private Long tenantId;

    private Long userId;

    private Integer source;

    private Integer status;

    private Date lastMessageTime;

    private Date createTime;
}
