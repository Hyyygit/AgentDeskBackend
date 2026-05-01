package com.agentdesk.common.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author hyyy
 * @date 2026/5/1 22:07
 * @description 获取Servlet相关参数的类
 */
public class ServletUtils {

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) requestAttributes;
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getParameter(name);
        }
        return null;
    }

    public static String getParameter(String name, String defaultValue) {
        String value = getParameter(name);
        return value != null ? value : defaultValue;
    }

    public static String[] getParameterValues(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getParameterValues(name);
        }
        return null;
    }

    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getHeader(name);
        }
        return null;
    }

    public static String getHeader(String name, String defaultValue) {
        String value = getHeader(name);
        return value != null ? value : defaultValue;
    }

    public static String getMethod() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getMethod();
        }
        return null;
    }

    public static String getRequestURI() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getRequestURI();
        }
        return null;
    }

    public static String getRemoteAddr() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        return null;
    }
}
