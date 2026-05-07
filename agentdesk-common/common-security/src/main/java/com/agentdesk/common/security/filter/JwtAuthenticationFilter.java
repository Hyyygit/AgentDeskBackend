package com.agentdesk.common.security.filter;

import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.common.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            try {
                Claims claims = jwtUtils.parseToken(token);
                setAuthentication(claims, request);
            } catch (Exception e) {
                log.warn("Failed to set user authentication: {}", e.getMessage());
            }
        } else if (StringUtils.hasText(request.getHeader("X-User-Id"))) {
            AuthUser authUser = new AuthUser();
            authUser.setUserId(Long.valueOf(request.getHeader("X-User-Id")));
            authUser.setUsername(request.getHeader("X-Username"));
            authUser.setTenantId(parseLong(request.getHeader("X-Tenant-Id")));

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(authUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserContext.setCurrentUser(authUser);
            request.setAttribute("currentUser", authUser);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private void setAuthentication(Claims claims, HttpServletRequest request) {
        AuthUser authUser = new AuthUser();
        authUser.setUserId(claims.get("userId", Long.class));
        authUser.setUsername(claims.get("username", String.class));
        authUser.setRoleCode(claims.get("roleCode", String.class));
        authUser.setTenantId(claims.get("tenantId", Long.class));

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(authUser.getRoleCode())
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserContext.setCurrentUser(authUser);
        request.setAttribute("currentUser", authUser);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
