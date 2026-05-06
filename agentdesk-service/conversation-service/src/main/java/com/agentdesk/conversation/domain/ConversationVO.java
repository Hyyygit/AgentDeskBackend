package com.agentdesk.conversation.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/3 15:05
 * @description 传给前端的会话数据
 */
@Data
public class ConversationVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String conversationNo;

    private Long userId;

    private Integer source;

    private Integer status;

    private Date lastMessageTime;

    private Date createTime;
}
