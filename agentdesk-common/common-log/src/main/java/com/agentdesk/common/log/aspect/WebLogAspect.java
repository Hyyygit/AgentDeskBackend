package com.agentdesk.common.log.aspect;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class WebLogAspect {

    @Pointcut("execution(* com.agentdesk..controller..*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get("traceId");
        long start = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String url = request != null ? request.getRequestURL().toString() : "unknown";
        String method = request != null ? request.getMethod() : "unknown";
        String ip = request != null ? request.getRemoteAddr() : "unknown";
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        String params = JSONUtil.toJsonStr(joinPoint.getArgs());

        log.info("[traceId:{}] Request - URL:{}, Method:{}, IP:{}, ClassMethod:{}, Params:{}",
                traceId, url, method, ip, classMethod, params);

        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        String responseStr = JSONUtil.toJsonStr(result);
        if (responseStr != null && responseStr.length() > 1000) {
            responseStr = responseStr.substring(0, 1000) + "...";
        }

        log.info("[traceId:{}] Response - ClassMethod:{}, Result:{}, Elapsed:{}ms",
                traceId, classMethod, responseStr, elapsed);

        return result;
    }
}
