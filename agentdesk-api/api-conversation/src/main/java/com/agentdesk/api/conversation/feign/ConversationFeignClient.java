package com.agentdesk.api.conversation.feign;

import com.agentdesk.api.conversation.dto.ConversationDTO;
import com.agentdesk.api.conversation.dto.ConversationMessageDTO;
import com.agentdesk.api.conversation.dto.SendMessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "conversation-service", path = "/internal/conversations")
public interface ConversationFeignClient {

    @PostMapping("/messages")
    ConversationMessageDTO sendMessage(@RequestBody SendMessageRequest request);

    @GetMapping("/{conversationId}")
    ConversationDTO getConversation(@PathVariable Long conversationId);

    @GetMapping("/{conversationId}/messages")
    List<ConversationMessageDTO> getMessages(@PathVariable Long conversationId);
}
