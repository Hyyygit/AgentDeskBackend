package com.agentdesk.common.web.result;

import cn.hutool.core.util.ObjectUtil;
import com.agentdesk.common.core.enums.HttpStatus;


import java.io.Serial;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author hyyy
 * @date 2026/4/30 00:15
 * @description 异步调用结果类
 */
public class AjaxResult extends HashMap<String, Object> {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String CODE = "code"; //状态码
    public static final String MSG = "message"; //消息
    public static final String DATA = "data"; //数据
    public static final String TIME_STAMP = "timestamp"; //时间戳

    public AjaxResult() {//构造函数，返回一个空信息
    }

    public AjaxResult(int code, String message) {//只有状态码和信息的构造函数
        super.put(CODE, code);
        super.put(MSG, message);
        super.put(TIME_STAMP, System.currentTimeMillis());
    }

    public AjaxResult(int code, String message, Object data) {// 状态码、信息、数据的构造函数
        super.put(CODE, code);
        super.put(MSG, message);
        super.put(TIME_STAMP, System.currentTimeMillis());
        if (ObjectUtil.isNotNull(data)) {
            super.put(DATA, data);
        }
    }

    public static AjaxResult success() {//静态函数，提示成功
        return AjaxResult.success("操作成功");
    }

    public static AjaxResult success(Object data) {//静态函数，只有数据
        return AjaxResult.success("操作成功", data);
    }

    public static AjaxResult success(String message) {//静态函数，只有信息，没有数据
        return new AjaxResult(HttpStatus.OK.getCode(), message);
    }

    public static AjaxResult success(String message, Object data) {//静态函数，返回成功信息
        return new AjaxResult(HttpStatus.OK.getCode(), message, data);
    }

    public static AjaxResult warn(String message) {//不带数据的警告信息
        return new AjaxResult(HttpStatus.WARN.getCode(), message);
    }

    public static AjaxResult warn(String message, Object data) {//返回警告信息和数据
        return new AjaxResult(HttpStatus.WARN.getCode(), message, data);
    }

    public static AjaxResult error() {
        return AjaxResult.error("操作失败");
    }

    public static AjaxResult error(Object data) {//静态函数，返回错误信息，包含数据
        return AjaxResult.error("操作失败", data);
    }

    public static AjaxResult error(String message) {//静态函数，返回错误信息，不包含数据
        return new AjaxResult(HttpStatus.ERROR.getCode(), message);
    }

    public static AjaxResult error(String message, Object data) {//返回错误信息和数据
        return new AjaxResult(HttpStatus.ERROR.getCode(), message, data);
    }

    public boolean isSuccess() {//判断是否是成功类型的响应
        return Objects.equals(HttpStatus.OK.getCode(), this.get(CODE));
    }

    public boolean isWarn() {//判断是否是成功类型的响应
        return Objects.equals(HttpStatus.WARN.getCode(), this.get(CODE));
    }

    public boolean isError() {//判断是否是成功类型的响应
        return Objects.equals(HttpStatus.ERROR.getCode(), this.get(CODE));
    }
}
