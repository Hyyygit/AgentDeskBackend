package com.agentdesk.common.core.exception.base;

import lombok.AllArgsConstructor;

import java.io.Serial;

/**
 * @author hyyy
 * @date 2026/4/29 23:52
 * @description 基础异常类
 */
@AllArgsConstructor
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    // 错误码
    private int code;

    // 错误信息
    private String message;

    // 所属服务
    private String service;

    // 错误码对应参数
    private Object[] args;

    public BaseException(int code, String message){
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String message, String service){
        this.code = code;
        this.message = message;
        this.service = service;
    }
}
