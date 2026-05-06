package com.agentdesk.notification.service;

import com.agentdesk.notification.domain.NotificationPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface INotificationService extends IService<NotificationPO> {

    NotificationPO send(Long userId, String title, String content, String type);

    void markAsRead(Long notificationId);

    List<NotificationPO> listByUser(Long userId);

    Long getUnreadCount(Long userId);
}
