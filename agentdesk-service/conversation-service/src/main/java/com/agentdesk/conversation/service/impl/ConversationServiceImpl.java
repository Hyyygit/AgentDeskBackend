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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hyyy
 * @date 2026/5/3 15:12
 * @description 会话服务实现类
 */
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, ConversationPO> implements IConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationServiceImpl.class);
    private final ExecutorService streamExecutor = Executors.newCachedThreadPool();

    @Value("${orchestrator.url:http://localhost:9005}")
    private String orchestratorUrl;

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
        streamExecutor.submit(() -> {
            String agentContent = null;
            String requestId = IdUtil.fastSimpleUUID();
            try {
                Long userId = UserContext.getCurrentUserId();

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

                WebClient webClient = WebClient.builder()
                    .baseUrl(orchestratorUrl)
                    .build();

                StringBuilder agentContentBuilder = new StringBuilder();

                webClient.post()
                    .uri("/internal/agent/orchestrate/stream")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(orchRequest)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(data -> {
                        try {
                            agentContentBuilder.append(data);
                            emitter.send(SseEmitter.event().data(data));
                        } catch (IOException e) {
                            log.warn("SSE send failed", e);
                        }
                    })
                    .doOnComplete(() -> {
                        try {
                            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                        } catch (IOException e) {
                            log.warn("SSE done send failed", e);
                        }
                    })
                    .blockLast(Duration.ofMinutes(5));

                agentContent = agentContentBuilder.toString();

                ConversationMessagePO agentMessage = new ConversationMessagePO();
                agentMessage.setConversationId(conversationId);
                agentMessage.setRequestId(requestId);
                agentMessage.setSenderId(0L);
                agentMessage.setSenderType(2);
                agentMessage.setMessageType(1);
                agentMessage.setContent(agentContent);
                conversationMessageMapper.insert(agentMessage);

                ConversationPO conversation = getById(conversationId);
                if (conversation != null) {
                    conversation.setLastMessageTime(LocalDateTime.now());
                    updateById(conversation);
                }

                emitter.complete();
            } catch (Exception e) {
                log.error("SSE stream failed for conversationId={}", conversationId, e);
                try {
                    if (agentContent == null) {
                        emitter.send(SseEmitter.event().data("抱歉，系统处理您的请求时遇到了问题，请稍后重试。"));
                    }
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
    }
}
