package com.we.mall.common.security.util;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.security.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Set;

/**
 * 安全工具类
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    // ==================== 会话 ====================

    public static String getSessionId() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getSessionId();
    }

    public static String getClientType() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getClientType();
    }

    public static String getDeviceType() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getDeviceType();
    }

    // ==================== 用户 ====================

    public static Long getUserId() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getUserId();
    }

    public static Long requireUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw UnauthorizedException.notLogin();
        }
        return userId;
    }

    public static String getUsername() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getUsername();
    }

    public static String requireUsername() {
        String username = getUsername();
        if (username == null) {
            throw UnauthorizedException.notLogin();
        }
        return username;
    }

    public static String getNickname() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getNickname();
    }

    public static String getAvatar() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null ? null : ctx.getAvatar();
    }

    // ==================== 授权 ====================

    public static Set<String> getRoles() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null || ctx.getRoles() == null ? Collections.emptySet() : ctx.getRoles();
    }

    public static Set<String> getPerms() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx == null || ctx.getPerms() == null ? Collections.emptySet() : ctx.getPerms();
    }

    public static boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public static boolean hasPermission(String perm) {
        return getPerms().contains(perm);
    }

    /**
     * 要求已登录，未登录抛 401
     */
    public static void requireLogin() {
        if (!isLogin()) {
            throw UnauthorizedException.notLogin();
        }
    }

    // ==================== 判断 ====================

    public static boolean isLogin() {
        return SecurityContextHolder.getContext() != null;
    }

    public static boolean isAdmin() {
        return ClientType.ADMIN.getCode().equalsIgnoreCase(getClientType());
    }

    public static boolean isMember() {
        return ClientType.MEMBER.getCode().equalsIgnoreCase(getClientType());
    }

}
