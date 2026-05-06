package com.agentdesk.api.ticket.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
}
