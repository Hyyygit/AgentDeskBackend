package com.agentdesk.common.feign.config;

import com.agentdesk.common.security.context.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                requestTemplate.header("X-User-Id", String.valueOf(userId));
            }

            Long tenantId = UserContext.getCurrentTenantId();
            if (tenantId != null) {
                requestTemplate.header("X-Tenant-Id", String.valueOf(tenantId));
            }

            String traceId = MDC.get("traceId");
            if (traceId != null) {
                requestTemplate.header("X-Trace-Id", traceId);
            }
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 100, 1);
    }

}
