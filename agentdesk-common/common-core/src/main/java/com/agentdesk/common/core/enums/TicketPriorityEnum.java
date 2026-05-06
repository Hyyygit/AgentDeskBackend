package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:42
 * @description 工单优先级枚举
 */
@Getter
public enum TicketPriorityEnum {
    P1(1, "紧急"),
    P2(2, "高"),
    P3(3, "中"),
    P4(4, "低");

    private final Integer level;
    private final String description;

    TicketPriorityEnum(Integer level, String description) {
        this.level = level;
        this.description = description;
    }
}
