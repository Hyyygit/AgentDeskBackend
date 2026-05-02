package com.agentdesk.conversation.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/2 15:17
 * @description 会话持久化对象
 */
@Data
public class ConversationPO extends BaseEntity {

    private String conversationNo;//会话编号
}
