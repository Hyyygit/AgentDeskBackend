package com.agentdesk.conversation.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 会话消息视图对象
 */
@Data
public class ConversationMessageVO implements Serializable {

    @Serial
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
