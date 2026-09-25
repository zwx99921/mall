package com.we.mall.common.session.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

import java.util.Collections;
import java.util.Set;

/**
 * 会话上下文
 * <p>
 * 全应用唯一的用户上下文，由 common-security 的认证拦截器 set / clear。
 * 业务代码通过本类拿当前登录用户信息。
 * <p>
 * 使用 {@link TransmittableThreadLocal}，支持线程池透传。
 * <p>
 * 本类只负责「存储」，不依赖 common-security，避免循环依赖。
 * 需要「未登录抛 401」的语义，请用 common-security 的 SecurityContext / SecurityUtils。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SessionContext {

    private static final TransmittableThreadLocal<SessionInfo> HOLDER = new TransmittableThreadLocal<>();

    private SessionContext() {
    }

    // ==================== 基础 ====================

    /**
     * 设置会话
     */
    public static void set(SessionInfo info) {
        HOLDER.set(info);
    }

    /**
     * 获取会话，未登录返回 null
     */
    public static SessionInfo get() {
        return HOLDER.get();
    }

    /**
     * 清除会话
     */
    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return HOLDER.get() != null;
    }

    // ==================== 会话字段 ====================

    /**
     * 会话 ID
     */
    public static String getSessionId() {
        SessionInfo info = HOLDER.get();
        return info == null ? null : info.getSessionId();
    }

    /**
     * 客户端类型：admin / member
     */
    public static String getClientType() {
        SessionInfo info = HOLDER.get();
        return info == null ? null : info.getClientType();
    }

    /**
     * 设备类型：PC / H5 / APP / MINI
     */
    public static String getDeviceType() {
        SessionInfo info = HOLDER.get();
        return info == null ? null : info.getDeviceType();
    }

    // ==================== 用户字段 ====================

    /**
     * 当前用户，未登录返回 null
     */
    public static SessionUser getUser() {
        SessionInfo info = HOLDER.get();
        return info == null ? null : info.getUser();
    }

    /**
     * 当前用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        SessionUser user = getUser();
        return user == null ? null : user.getUserId();
    }

    /**
     * 当前用户名，未登录返回 null
     */
    public static String getUsername() {
        SessionUser user = getUser();
        return user == null ? null : user.getUsername();
    }

    /**
     * 当前昵称，未登录返回 null
     */
    public static String getNickname() {
        SessionUser user = getUser();
        return user == null ? null : user.getNickname();
    }

    /**
     * 当前租户 ID，未登录返回 null
     */
    public static Long getTenantId() {
        SessionUser user = getUser();
        return user == null ? null : user.getTenantId();
    }

    // ==================== 角色 / 权限 ====================

    /**
     * 当前角色，未登录返回空 Set
     */
    public static Set<String> getRoles() {
        SessionInfo info = HOLDER.get();
        return info == null || info.getRoles() == null
                ? Collections.emptySet()
                : info.getRoles();
    }

    /**
     * 当前权限，未登录返回空 Set
     */
    public static Set<String> getPerms() {
        SessionInfo info = HOLDER.get();
        return info == null || info.getPerms() == null
                ? Collections.emptySet()
                : info.getPerms();
    }

    // ==================== 端类型快捷判断 ====================

    /**
     * 是否管理端
     */
    public static boolean isAdmin() {
        return ClientType.ADMIN.getCode().equalsIgnoreCase(getClientType());
    }

    /**
     * 是否 C 端
     */
    public static boolean isMember() {
        return ClientType.MEMBER.getCode().equalsIgnoreCase(getClientType());
    }

}
