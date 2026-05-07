package com.agentdesk.conversation.controller;

import com.agentdesk.api.conversation.dto.MessageFeedbackRequest;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.agentdesk.conversation.mapper.ConversationMessageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 会话消息控制器
 */
@Tag(name = "会话消息管理")
@RestController
@RequestMapping("/conversations")
public class ConversationMessageController extends BaseController {

    @Autowired
    private ConversationMessageMapper conversationMessageMapper;

    @Operation(summary = "消息反馈")
    @PostMapping("/{messageId}/feedback")
    public Object feedback(@PathVariable Long messageId, @RequestBody MessageFeedbackRequest request) {
        ConversationMessagePO message = conversationMessageMapper.selectById(messageId);
        if (message == null) {
            return error("消息不存在");
        }
        message.setExtraJson("{\"rating\":" + request.getRating() + ", \"comment\":\"" + request.getComment() + "\"}");
        conversationMessageMapper.updateById(message);
        return success("反馈成功");
    }
}
