package com.agentdesk.common.log.filter;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = IdUtil.fastSimpleUUID();
        MDC.put("traceId", traceId);
        try {
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader("X-Trace-Id", traceId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
