package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 19:25
 * @description 转给人工处理的类型枚举
 */
@RequiredArgsConstructor
@Getter
public enum HumanHandoffTypeEnum {
    LOW_CONFIDENCE(1, "置信度过低"),
    SENSITIVE(2, "敏感信息"),
    TOOL_FAILED(3, "工具调用失败");

    private final int type;
    private final String description;
}
