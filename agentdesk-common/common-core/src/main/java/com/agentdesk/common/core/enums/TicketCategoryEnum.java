package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:45
 * @description 工单类别枚举
 */
@Getter
public enum TicketCategoryEnum {
    ACCOUNT_ACCESS(1, "账号访问"),
    SYSTEM_BUG(2, "系统故障"),
    NETWORK_FAILURE(3, "网络问题"),
    FINANCE_PROCESS(4, "财务流程"),
    PERMISSION_REQUEST(5, "权限申请"),
    COMPLAINT(6, "投诉"),
    CONSULTATION(7, "咨询"),
    GENERAL_SUPPORT(8, "通用支持");

    private final Integer category;
    private final String description;

    TicketCategoryEnum(Integer category, String description) {
        this.category = category;
        this.description = description;
    }
}
