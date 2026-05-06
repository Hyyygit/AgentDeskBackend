package com.agentdesk.common.test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Map<String, Object> createMockUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("userId", UUID.randomUUID().toString());
        user.put("username", "testuser");
        user.put("email", "test@example.com");
        user.put("phone", "13800138000");
        user.put("status", 1);
        user.put("createTime", LocalDateTime.now());
        user.put("updateTime", LocalDateTime.now());
        return user;
    }

    public static Map<String, Object> createMockTicket() {
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("id", 1L);
        ticket.put("ticketId", UUID.randomUUID().toString());
        ticket.put("title", "Test Ticket");
        ticket.put("description", "This is a test ticket");
        ticket.put("status", "OPEN");
        ticket.put("priority", "MEDIUM");
        ticket.put("assigneeId", 1L);
        ticket.put("creatorId", 1L);
        ticket.put("createTime", LocalDateTime.now());
        ticket.put("updateTime", LocalDateTime.now());
        return ticket;
    }

    public static Map<String, Object> createMockConversation() {
        Map<String, Object> conversation = new HashMap<>();
        conversation.put("id", 1L);
        conversation.put("conversationId", UUID.randomUUID().toString());
        conversation.put("ticketId", 1L);
        conversation.put("senderId", 1L);
        conversation.put("senderType", "USER");
        conversation.put("content", "This is a test message");
        conversation.put("messageType", "TEXT");
        conversation.put("createTime", LocalDateTime.now());
        return conversation;
    }
}
