package com.agentdesk.notification.controller;

import com.agentdesk.api.notification.dto.SendNotificationRequest;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.notification.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class NotificationInternalController extends BaseController {

    private final INotificationService notificationService;

    @Operation(summary = "发送通知(内部调用)")
    @PostMapping("/send")
    public AjaxResult send(@Valid @RequestBody SendNotificationRequest request) {
        notificationService.send(request.getUserId(), request.getTitle(),
                request.getContent(), request.getType());
        return success("发送成功");
    }
}
