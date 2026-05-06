package com.agentdesk.conversation.service;

import com.agentdesk.api.conversation.dto.SendMessageRequest;
import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.agentdesk.conversation.domain.ConversationPO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 15:12
 * @description 会话数据服务层
 */
public interface IConversationService extends IService<ConversationPO> {

    ConversationPO createConversation(Long userId, Integer source);

    ConversationPO getConversation(Long conversationId);

    List<ConversationPO> listUserConversations(Long userId);

    ConversationMessagePO sendMessage(SendMessageRequest request);

    List<ConversationMessagePO> getMessages(Long conversationId);

    void closeConversation(Long conversationId);

    void sendStreamMessage(Long conversationId, String content, SseEmitter emitter);
}
