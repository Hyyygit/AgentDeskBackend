package com.agentdesk.common.log.aspect;

import cn.hutool.json.JSONUtil;
import com.agentdesk.common.log.annotation.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditLogAspect {

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String traceId = MDC.get("traceId");
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String params = JSONUtil.toJsonStr(args);

        log.info("[traceId:{}] AuditLog start - method:{}, module:{}, operation:{}, params:{}",
                traceId, methodName, auditLog.module(), auditLog.operation(), params);

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        log.info("[traceId:{}] AuditLog end - method:{}, module:{}, operation:{}, result:{}, elapsed:{}ms",
                traceId, methodName, auditLog.module(), auditLog.operation(),
                JSONUtil.toJsonStr(result), elapsed);

        return result;
    }
}
