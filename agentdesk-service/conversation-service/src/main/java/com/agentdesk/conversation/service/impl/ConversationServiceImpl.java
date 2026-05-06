package com.agentdesk.conversation.service.impl;

import cn.hutool.core.util.IdUtil;
import com.agentdesk.api.conversation.dto.SendMessageRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import com.agentdesk.api.orchestrator.feign.OrchestratorFeignClient;
import com.agentdesk.common.core.enums.ErrorCodeEnum;
import com.agentdesk.common.core.exception.base.BaseException;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.agentdesk.conversation.domain.ConversationPO;
import com.agentdesk.conversation.mapper.ConversationMapper;
import com.agentdesk.conversation.mapper.ConversationMessageMapper;
import com.agentdesk.conversation.service.IConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 15:12
 * @description 会话服务实现类
 */
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, ConversationPO> implements IConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMessageMapper conversationMessageMapper;

    @Autowired
    private OrchestratorFeignClient orchestratorClient;

    @Override
    public ConversationPO createConversation(Long userId, Integer source) {
        ConversationPO conversation = new ConversationPO();
        conversation.setConversationNo(IdUtil.fastSimpleUUID());
        conversation.setUserId(userId);
        conversation.setSource(source != null ? source : 1);
        conversation.setStatus(1);
        conversation.setLastMessageTime(LocalDateTime.now());
        save(conversation);
        return conversation;
    }

    @Override
    public ConversationPO getConversation(Long conversationId) {
        ConversationPO conversation = getById(conversationId);
        if (conversation == null) {
            throw new BaseException(ErrorCodeEnum.NOT_FOUND.getCode(), "会话不存在");
        }
        return conversation;
    }

    @Override
    public List<ConversationPO> listUserConversations(Long userId) {
        return conversationMapper.selectByUserId(userId);
    }

    @Override
    public ConversationMessagePO sendMessage(SendMessageRequest request) {
        Long userId = UserContext.getCurrentUserId();

        Long conversationId = request.getConversationId();
        if (conversationId == null) {
            ConversationPO conversation = createConversation(userId, 1);
            conversationId = conversation.getId();
        }

        ConversationMessagePO userMessage = new ConversationMessagePO();
        userMessage.setConversationId(conversationId);
        userMessage.setRequestId(IdUtil.fastSimpleUUID());
        userMessage.setSenderId(userId);
        userMessage.setSenderType(1);
        userMessage.setMessageType(request.getMessageType() != null ? request.getMessageType() : 1);
        userMessage.setContent(request.getContent());
        conversationMessageMapper.insert(userMessage);

        OrchestrateRequest orchRequest = new OrchestrateRequest();
        orchRequest.setContent(request.getContent());
        orchRequest.setConversationId(conversationId);
        orchRequest.setUserId(userId);
        orchRequest.setRequestId(userMessage.getRequestId());

        OrchestrateResponse orchResponse = orchestratorClient.orchestrate(orchRequest);

        ConversationMessagePO agentMessage = new ConversationMessagePO();
        agentMessage.setConversationId(conversationId);
        agentMessage.setRequestId(orchResponse.getRequestId());
        agentMessage.setSenderId(0L);
        agentMessage.setSenderType(2);
        agentMessage.setMessageType(1);
        agentMessage.setContent(orchResponse.getReplyContent());
        conversationMessageMapper.insert(agentMessage);

        ConversationPO conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setLastMessageTime(LocalDateTime.now());
            updateById(conversation);
        }

        return agentMessage;
    }

    @Override
    public List<ConversationMessagePO> getMessages(Long conversationId) {
        return conversationMessageMapper.selectByConversationId(conversationId);
    }

    @Override
    public void closeConversation(Long conversationId) {
        ConversationPO conversation = getById(conversationId);
        if (conversation == null) {
            throw new BaseException(ErrorCodeEnum.NOT_FOUND.getCode(), "会话不存在");
        }
        conversation.setStatus(2);
        updateById(conversation);
    }

    @Override
    public void sendStreamMessage(Long conversationId, String content, SseEmitter emitter) {
        try {
            Long userId = UserContext.getCurrentUserId();

            String requestId = IdUtil.fastSimpleUUID();

            ConversationMessagePO userMessage = new ConversationMessagePO();
            userMessage.setConversationId(conversationId);
            userMessage.setRequestId(requestId);
            userMessage.setSenderId(userId);
            userMessage.setSenderType(1);
            userMessage.setMessageType(1);
            userMessage.setContent(content);
            conversationMessageMapper.insert(userMessage);

            OrchestrateRequest orchRequest = new OrchestrateRequest();
            orchRequest.setContent(content);
            orchRequest.setConversationId(conversationId);
            orchRequest.setUserId(userId);
            orchRequest.setRequestId(requestId);

            OrchestrateResponse orchResponse = orchestratorClient.orchestrate(orchRequest);

            String replyContent = orchResponse.getReplyContent();
            if (replyContent != null) {
                for (char c : replyContent.toCharArray()) {
                    emitter.send(SseEmitter.event().data(String.valueOf(c)));
                }
            }

            ConversationMessagePO agentMessage = new ConversationMessagePO();
            agentMessage.setConversationId(conversationId);
            agentMessage.setRequestId(orchResponse.getRequestId());
            agentMessage.setSenderId(0L);
            agentMessage.setSenderType(2);
            agentMessage.setMessageType(1);
            agentMessage.setContent(replyContent);
            conversationMessageMapper.insert(agentMessage);

            ConversationPO conversation = getById(conversationId);
            if (conversation != null) {
                conversation.setLastMessageTime(LocalDateTime.now());
                updateById(conversation);
            }

            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
