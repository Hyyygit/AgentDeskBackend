package com.agentdesk.conversation.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hyyy
 * @date 2026/5/3 15:05
 * @description 传给前端的会话数据
 */
@Data
public class ConversationVO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private String conversationNo;//会话编号
}
