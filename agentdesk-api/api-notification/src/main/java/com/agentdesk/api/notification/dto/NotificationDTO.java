package com.agentdesk.api.notification.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Boolean read;
    private Date createTime;
}
