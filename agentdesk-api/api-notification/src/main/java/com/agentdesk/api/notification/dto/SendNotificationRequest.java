package com.agentdesk.api.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendNotificationRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String type = "SYSTEM";
}
