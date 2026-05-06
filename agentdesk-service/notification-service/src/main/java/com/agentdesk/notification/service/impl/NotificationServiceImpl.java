package com.agentdesk.notification.service.impl;

import com.agentdesk.notification.domain.NotificationPO;
import com.agentdesk.notification.mapper.NotificationMapper;
import com.agentdesk.notification.service.INotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, NotificationPO> implements INotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public NotificationPO send(Long userId, String title, String content, String type) {
        NotificationPO notification = new NotificationPO();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type != null ? type : "SYSTEM");
        notification.setIsRead(false);
        save(notification);
        log.info("发送通知: userId={}, title={}", userId, title);
        return notification;
    }

    @Override
    public void markAsRead(Long notificationId) {
        NotificationPO notification = getById(notificationId);
        if (notification != null) {
            notification.setIsRead(true);
            updateById(notification);
        }
    }

    @Override
    public List<NotificationPO> listByUser(Long userId) {
        return notificationMapper.selectByUserId(userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return lambdaQuery()
                .eq(NotificationPO::getUserId, userId)
                .eq(NotificationPO::getIsRead, false)
                .count();
    }
}
