package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:45
 * @description 工单类别枚举
 */
@RequiredArgsConstructor
@Getter
public enum TicketCategoryEnum {
    ACCOUNT_ACCESS("账号访问"),
    SYSTEM_BUG("系统故障"),
    NETWORK_FAILURE("网络问题"),
    FINANCE_PROCESS("财务流程"),
    PERMISSION_REQUEST("权限申请"),
    COMPLAINT("投诉"),
    CONSULTATION("咨询"),
    GENERAL_SUPPORT("通用支持");

    private final String description;
}
