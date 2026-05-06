package com.agentdesk.api.notification.feign;

import com.agentdesk.api.notification.dto.SendNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/internal/notifications")
public interface NotificationFeignClient {

    @PostMapping("/send")
    void send(@RequestBody SendNotificationRequest request);
}
