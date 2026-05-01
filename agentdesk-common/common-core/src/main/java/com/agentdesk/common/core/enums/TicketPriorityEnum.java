package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:42
 * @description 工单优先级枚举
 */
@RequiredArgsConstructor
@Getter
public enum TicketPriorityEnum {
    P1("紧急"),
    P2("高"),
    P3("中"),
    P4("低");

    private final String description;
}
