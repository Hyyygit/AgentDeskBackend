package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 18:59
 * @description 消息是由谁发出的枚举类
 */
@Getter
public enum MessageSourceTypeEnum {
    USER(1, "用户"),
    AGENT(2,"智能体"),
    HUMAN(3, "人工"),
    SYSTEM(4, "系统");

    private final int source;
    private final String description;

    MessageSourceTypeEnum(int source, String description) {
        this.source = source;
        this.description = description;
    }
}
