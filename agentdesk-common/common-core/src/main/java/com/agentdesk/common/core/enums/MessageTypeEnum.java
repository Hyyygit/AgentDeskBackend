package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/5/3 19:02
 * @description 消息类型枚举类
 */
@RequiredArgsConstructor
@Getter
public enum MessageTypeEnum {
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    FILE(3, "文件");

    private final int type;
    private final String description;
}
