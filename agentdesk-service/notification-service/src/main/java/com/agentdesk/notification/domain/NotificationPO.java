package com.agentdesk.notification.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class NotificationPO extends BaseEntity {

    private Long userId;

    private String title;

    private String content;

    private String type;

    private Boolean isRead;
}
