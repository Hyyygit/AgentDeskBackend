package com.agentdesk.conversation.controller;

import com.agentdesk.conversation.service.IConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyyy
 * @date 2026/5/3 14:58
 * @description 会话管理器
 */
@Tag(name = "会话管理")
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final IConversationService conversationService;
}
