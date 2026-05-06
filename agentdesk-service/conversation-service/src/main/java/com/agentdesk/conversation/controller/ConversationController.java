package com.agentdesk.conversation.controller;

import com.agentdesk.api.conversation.dto.SendMessageRequest;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.conversation.converter.ConversationConverter;
import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.agentdesk.conversation.domain.ConversationPO;
import com.agentdesk.conversation.service.IConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hyyy
 * @date 2026/5/3 14:58
 * @description 会话管理器
 */
@Tag(name = "会话管理")
@RestController
@RequestMapping("/api/conversations")
public class ConversationController extends BaseController {

    @Autowired
    private IConversationService conversationService;

    @Operation(summary = "创建会话")
    @PostMapping
    public Object createConversation() {
        Long userId = UserContext.getCurrentUserId();
        ConversationPO conversation = conversationService.createConversation(userId, 1);
        return success(ConversationConverter.toVO(conversation));
    }

    @Operation(summary = "获取用户会话列表")
    @GetMapping
    public Object listUserConversations() {
        Long userId = UserContext.getCurrentUserId();
        List<ConversationPO> list = conversationService.listUserConversations(userId);
        return success(list.stream().map(ConversationConverter::toVO).collect(Collectors.toList()));
    }

    @Operation(summary = "获取会话详情")
    @GetMapping("/{conversationId}")
    public Object getConversation(@PathVariable Long conversationId) {
        ConversationPO conversation = conversationService.getConversation(conversationId);
        return success(ConversationConverter.toVO(conversation));
    }

    @Operation(summary = "发送消息")
    @PostMapping("/messages")
    public Object sendMessage(@Valid @RequestBody SendMessageRequest request) {
        ConversationMessagePO message = conversationService.sendMessage(request);
        return success(ConversationConverter.toMessageVO(message));
    }

    @Operation(summary = "获取会话消息列表")
    @GetMapping("/{conversationId}/messages")
    public Object getMessages(@PathVariable Long conversationId) {
        List<ConversationMessagePO> messages = conversationService.getMessages(conversationId);
        return success(messages.stream().map(ConversationConverter::toMessageVO).collect(Collectors.toList()));
    }

    @Operation(summary = "关闭会话")
    @PutMapping("/{conversationId}/close")
    public Object closeConversation(@PathVariable Long conversationId) {
        conversationService.closeConversation(conversationId);
        return success("会话已关闭");
    }

    @Operation(summary = "流式消息")
    @GetMapping(value = "/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@RequestParam Long conversationId, @RequestParam String content) {
        SseEmitter emitter = new SseEmitter(300000L);
        conversationService.sendStreamMessage(conversationId, content, emitter);
        return emitter;
    }
}
