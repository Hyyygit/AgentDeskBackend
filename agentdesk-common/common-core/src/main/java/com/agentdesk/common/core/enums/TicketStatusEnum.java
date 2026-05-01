package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:37
 * @description 工单状态枚举
 */
@RequiredArgsConstructor
@Getter
public enum TicketStatusEnum {
    NEW("新建"),
    TRIAGED("已分诊"),
    DECIDED("已决策"),
    IN_PROGRESS("处理中"),
    WAITING_HUMAN("等待人工"),
    RESOLVED("已解决"),
    CLOSED("已关闭");

    private final String description;
}
