package com.agentdesk.api.event;

public final class MqConstants {
    public static final String EXCHANGE_NAME = "agentdesk.exchange";
    
    public static final String QUEUE_NOTIFICATION = "notification.queue";
    public static final String QUEUE_AUDIT = "audit.queue";
    public static final String QUEUE_CURATION = "curation.queue";
    public static final String QUEUE_DRAFT = "draft.queue";
    
    public static final String KEY_TICKET_CREATED = "ticket.created";
    public static final String KEY_TICKET_STATUS_CHANGED = "ticket.status.changed";
    public static final String KEY_TICKET_RESOLVED = "ticket.resolved";
    public static final String KEY_AGENT_RUN = "agent.run";
    public static final String KEY_KNOWLEDGE_DRAFT = "knowledge.draft";
    
    private MqConstants() {}
}
