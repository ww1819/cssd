package com.cssd.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static LoginUser current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser user) {
            return user;
        }
        return null;
    }

    public static LoginUser requireUser() {
        LoginUser user = current();
        if (user == null) {
            throw new com.cssd.common.BizException(401, "未登录");
        }
        return user;
    }
}
