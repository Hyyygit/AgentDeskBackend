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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();
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

                RestTemplate restTemplate = new RestTemplate();
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setBufferRequestBody(false);
                factory.setConnectTimeout(300000);
                factory.setReadTimeout(300000);
                restTemplate.setRequestFactory(factory);

                String streamUrl = orchestratorUrl + "/internal/agent/orchestrate/stream";
                StringBuilder agentContentBuilder = new StringBuilder();

                restTemplate.execute(streamUrl, HttpMethod.POST,
                    req -> {
                        req.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                        req.getBody().write(objectMapper.writeValueAsBytes(orchRequest));
                    },
                    res -> {
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(res.getBody(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data:")) {
                                String data = line.substring(5);
                                if ("[DONE]".equals(data.trim())) {
                                    emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                                } else {
                                    agentContentBuilder.append(data);
                                    emitter.send(SseEmitter.event().data(data));
                                }
                            }
                        }
                        return null;
                    });

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
