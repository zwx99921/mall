package com.we.mall.common.security.util;

import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

import java.util.Set;

/**
 * 安全工具（业务入口）
 * <p>
 * 业务代码只依赖本类，不直接依赖 SecurityContext / SessionContext。
 * <p>
 * 语义约定：
 * <ul>
 *     <li>{@code getXxx()}：未登录返回 null / 空集合</li>
 *     <li>{@code requireXxx()}：未登录抛 401</li>
 * </ul>
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    // ==================== 会话 ====================

    /**
     * 当前会话，未登录返回 null
     */
    public static SessionInfo getSession() {
        return SecurityContext.getSession();
    }

    /**
     * 当前会话 ID，未登录返回 null
     */
    public static String getSessionId() {
        SessionInfo info = SecurityContext.getSession();
        return info == null ? null : info.getSessionId();
    }

    // ==================== 用户 ====================

    /**
     * 当前用户，未登录返回 null
     */
    public static SessionUser getUser() {
        return SecurityContext.getUser();
    }

    /**
     * 当前用户，未登录抛 401
     */
    public static SessionUser requireUser() {
        return SecurityContext.requireUser();
    }

    /**
     * 当前用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        return SecurityContext.getUserId();
    }

    /**
     * 当前用户 ID，未登录抛 401
     */
    public static Long requireUserId() {
        return SecurityContext.requireUserId();
    }

    /**
     * 当前用户名，未登录返回 null
     */
    public static String getUsername() {
        return SecurityContext.getUsername();
    }

    /**
     * 当前昵称，未登录返回 null
     */
    public static String getNickname() {
        return SecurityContext.getNickname();
    }

    // ==================== 角色 / 权限 ====================

    /**
     * 当前角色，未登录返回空 Set
     */
    public static Set<String> getRoles() {
        return SecurityContext.getRoles();
    }

    /**
     * 当前权限，未登录返回空 Set
     */
    public static Set<String> getPerms() {
        return SecurityContext.getPerms();
    }

    // ==================== 状态判断 ====================

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return SecurityContext.isLogin();
    }

    /**
     * 是否管理端
     */
    public static boolean isAdmin() {
        return SecurityContext.isAdmin();
    }

    /**
     * 是否 C 端
     */
    public static boolean isMember() {
        return SecurityContext.isMember();
    }


}
