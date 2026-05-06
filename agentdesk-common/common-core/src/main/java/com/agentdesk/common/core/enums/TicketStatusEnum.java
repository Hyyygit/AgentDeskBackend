package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:37
 * @description 工单状态枚举
 */
@Getter
public enum TicketStatusEnum {
    NEW(1, "新建"),
    TRIAGED(2, "已分诊"),
    DECIDED(3, "已决策"),
    IN_PROGRESS(4, "处理中"),
    WAITING_HUMAN(5, "等待人工"),
    RESOLVED(6, "已解决"),
    CLOSED(7, "已关闭");

    private final int status;
    private final String description;

    TicketStatusEnum(int status, String description) {
        this.status = status;
        this.description = description;
    }
}
