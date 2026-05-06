package com.agentdesk.notification.consumer;

import com.agentdesk.api.event.MqConstants;
import com.agentdesk.api.event.TicketEvent;
import com.agentdesk.notification.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @Autowired
    private INotificationService notificationService;

    @RabbitListener(queues = MqConstants.QUEUE_NOTIFICATION)
    public void handleTicketEvent(TicketEvent event) {
        log.info("Received ticket event: type={}, ticketNo={}", event.getEventType(), event.getTicketNo());
        try {
            String title;
            String content;
            if ("CREATED".equals(event.getEventType())) {
                title = "工单创建通知";
                content = String.format("您的工单 %s 已创建，当前状态：新建", event.getTicketNo());
            } else if ("STATUS_CHANGED".equals(event.getEventType())) {
                title = "工单状态更新";
                content = String.format("您的工单 %s 状态已更新", event.getTicketNo());
            } else if ("RESOLVED".equals(event.getEventType())) {
                title = "工单已解决";
                content = String.format("您的工单 %s 已解决", event.getTicketNo());
            } else {
                title = "工单通知";
                content = String.format("工单 %s 有更新", event.getTicketNo());
            }
            notificationService.send(event.getUserId(), title, content, "SYSTEM");
        } catch (Exception e) {
            log.error("Failed to handle ticket event", e);
        }
    }
}
