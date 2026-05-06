package com.agentdesk.notification.controller;

import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.notification.domain.NotificationPO;
import com.agentdesk.notification.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "通知管理")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController extends BaseController {

    private final INotificationService notificationService;

    @Operation(summary = "获取当前用户通知列表")
    @GetMapping("/")
    public AjaxResult listNotifications() {
        Long userId = UserContext.getCurrentUserId();
        startPage();
        List<NotificationPO> list = notificationService.listByUser(userId);
        return success("查询成功", TableDataInfo.getTableDataInfo(list));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public AjaxResult markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return success("标记已读成功");
    }

    @Operation(summary = "获取未读通知数")
    @GetMapping("/unread-count")
    public AjaxResult getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return success(Map.of("count", count));
    }
}
