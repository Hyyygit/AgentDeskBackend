package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 19:16
 * @description 工单操作类型枚举类
 */
@Getter
public enum TicketActionTypeEnum {
    CREATE(1, "创建"),
    TRIAGE(2, "分诊"),
    DECIDE(3, "决策"),
    PROCESS(4, "处理"),
    WAIT_HUMAN(5, "等待人工"),
    RESOLVE(6, "解决"),
    CLOSE(7, "关闭");

    private final int type;
    private final String description;

    TicketActionTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }
}
