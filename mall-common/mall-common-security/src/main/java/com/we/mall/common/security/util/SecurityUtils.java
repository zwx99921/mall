package com.we.mall.common.security.util;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.context.SecurityContextHolder;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

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

    // ==================== SessionInfo ====================

    public static SessionInfo getSession() {
        return SecurityContextHolder.getSession();
    }

    public static boolean isLogin() {
        return getSession() != null;
    }

    public static String getSessionId() {
        SessionInfo s = getSession();
        return s == null ? null : s.getSessionId();
    }

    public static String getClientType() {
        SessionInfo s = getSession();
        return s == null ? null : s.getClientType();
    }

    public static String getDeviceType() {
        SessionInfo s = getSession();
        return s == null ? null : s.getDeviceType();
    }

    // ==================== SessionUser ====================

    public static SessionUser getLoginUser() {
        return SecurityContextHolder.getUser();
    }

    public static Long getUserId() {
        SessionUser u = getLoginUser();
        return u == null ? null : u.getUserId();
    }

    public static Long requireUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw UnauthorizedException.notLogin();
        }
        return userId;
    }

    public static SessionUser requireUser() {
        SessionUser u = getLoginUser();
        if (u == null) {
            throw UnauthorizedException.notLogin();
        }
        return u;
    }

    public static SessionInfo requireSession() {
        SessionInfo s = getSession();
        if (s == null) {
            throw UnauthorizedException.notLogin();
        }
        return s;
    }

    public static String getUsername() {
        SessionUser u = getLoginUser();
        return u == null ? null : u.getUsername();
    }

    public static String requireUsername() {
        String username = getUsername();
        if (username == null) {
            throw UnauthorizedException.notLogin();
        }
        return username;
    }

    public static String getNickname() {
        SessionUser u = getLoginUser();
        return u == null ? null : u.getNickname();
    }

    public static String getAvatar() {
        SessionUser u = getLoginUser();
        return u == null ? null : u.getAvatar();
    }

    // ==================== Auth ====================

    public static Set<String> getRoles() {
        SessionUser u = getLoginUser();
        return u == null || u.getRoles() == null ? Collections.emptySet() : u.getRoles();
    }

    public static Set<String> getPerms() {
        SessionUser u = getLoginUser();
        return u == null || u.getPerms() == null ? Collections.emptySet() : u.getPerms();
    }

    public static boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public static boolean hasPermission(String perm) {
        return getPerms().contains(perm);
    }

    // ==================== 端类型 ====================

    public static boolean isAdmin() {
        return ClientType.ADMIN.getCode().equalsIgnoreCase(getClientType());
    }

    public static boolean isMember() {
        return ClientType.MEMBER.getCode().equalsIgnoreCase(getClientType());
    }

}
