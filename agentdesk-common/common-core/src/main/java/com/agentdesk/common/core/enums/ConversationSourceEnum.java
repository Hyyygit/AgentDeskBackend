package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 17:41
 * @description 会话来源枚举类
 */
@Getter
public enum ConversationSourceEnum {
    WEB(1,"网页");

    private final int source;
    private final String description;

    ConversationSourceEnum(int source, String description) {
        this.source = source;
        this.description = description;
    }
}
