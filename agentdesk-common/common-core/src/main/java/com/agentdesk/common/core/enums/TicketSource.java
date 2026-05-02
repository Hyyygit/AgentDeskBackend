package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/2 15:51
 * @description 工单来源枚举
 */
@RequiredArgsConstructor
@Getter
public enum TicketSource {
    AI_AGENT(1, "AI-Agent");

    private final int source;
    private final String description;
}
