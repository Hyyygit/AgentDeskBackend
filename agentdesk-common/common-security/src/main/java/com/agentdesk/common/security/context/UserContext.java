package com.agentdesk.common.security.context;

import com.agentdesk.common.security.domain.AuthUser;

public class UserContext {

    private static final ThreadLocal<AuthUser> USER_HOLDER = new ThreadLocal<>();

    public static Long getCurrentUserId() {
        AuthUser user = USER_HOLDER.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUsername() {
        AuthUser user = USER_HOLDER.get();
        return user != null ? user.getUsername() : null;
    }

    public static Long getCurrentTenantId() {
        AuthUser user = USER_HOLDER.get();
        return user != null ? user.getTenantId() : null;
    }

    public static AuthUser getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static void setCurrentUser(AuthUser user) {
        USER_HOLDER.set(user);
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
