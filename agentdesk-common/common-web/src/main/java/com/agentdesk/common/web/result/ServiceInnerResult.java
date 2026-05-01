package com.agentdesk.common.web.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hyyy
 * @date 2026/4/30 00:16
 * @description 微服务间调用结果传递类
 */
@Getter
@Setter
@AllArgsConstructor
public class ServiceInnerResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;//成功状态码
    public static final int FAIL = 500;//失败状态码
    private int code;//状态码
    private String message;//信息
    private T data;//数据

    public static <T> ServiceInnerResult<T> success() {//静态函数，返回成功信息，包含状态码，不包含信息、数据
        return ServiceInnerResult.success("操作成功", null);
    }

    public static <T> ServiceInnerResult<T> success(T data) {//静态函数，返回成功信息，包含状态码、数据，不包含信息
        return ServiceInnerResult.success(null, data);
    }

    public static <T> ServiceInnerResult<T> success(String message) {//静态函数，返回成功信息，包含状态码、消息，不包含数据
        return ServiceInnerResult.success(message, null);
    }

    public static <T> ServiceInnerResult<T> success(String message, T data) {//静态函数，返回成功信息，包含状态码、信息、数据
        return restResult(SUCCESS, message, data);
    }

    public static <T> ServiceInnerResult<T> fail() {//静态函数，返回失败信息，包含状态码，不包含信息、数据
        return ServiceInnerResult.fail("操作失败", null);
    }

    public static <T> ServiceInnerResult<T> fail(T data) {//静态函数，返回失败信息，包含状态码、数据，不包含信息
        return ServiceInnerResult.fail(null, data);
    }

    public static <T> ServiceInnerResult<T> fail(String message) {//静态函数，返回失败信息，包含状态码、消息，不包含数据
        return ServiceInnerResult.fail(message, null);
    }

    public static <T> ServiceInnerResult<T> fail(String message, T data) {//静态函数，返回失败信息，包含状态码、信息、数据
        return restResult(FAIL, message, data);
    }

    public static <T> ServiceInnerResult<T> fail(int code, String message) {//静态函数，返回失败信息，说明失败状态码和消息
        return restResult(code, message, null);
    }

    private static <T> ServiceInnerResult<T> restResult(int code, String message, T data) {//静态函数，返回结果(方便其他函数写代码)
        return new ServiceInnerResult<>(code, message, data);
    }

    public static <T> boolean isFail(ServiceInnerResult<T> result) {//判断消息类别是否失败
        return !isSuccess(result);
    }

    public static <T> boolean isSuccess(ServiceInnerResult<T> result) {//判断消息类别是否成功
        return SUCCESS == result.getCode();
    }
}
