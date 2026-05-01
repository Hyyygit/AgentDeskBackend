package com.agentdesk.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author hyyy
 * @date 2026/4/29 23:34
 * @description 错误码枚举
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCodeEnum {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    private final int code;
    private final String message;
}
