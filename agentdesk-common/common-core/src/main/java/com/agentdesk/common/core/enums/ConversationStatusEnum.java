package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 17:38
 * @description 定义会话状态枚举类
 */
@Getter
public enum ConversationStatusEnum {
    ACTIVE(1, "启用中"),
    CLOSED(2, "已关闭");

    private final int status;
    private final String description;

    ConversationStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }
}
