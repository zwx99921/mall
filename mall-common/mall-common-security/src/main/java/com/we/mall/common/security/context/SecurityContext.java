package com.we.mall.common.security.context;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

import java.util.Set;

/**
 * 安全上下文门面
 * <p>
 * 本身不存储数据，全部代理 {@link SessionContext}。
 * 唯一职责：把「未登录」翻译成 {@link UnauthorizedException}（401）。
 * <p>
 * 分层：
 * <ul>
 *     <li>{@link SessionContext} 存储层，无 security 语义</li>
 *     <li>{@link SecurityContext} 门面层，加「未登录 → 401」语义</li>
 *     <li>SecurityUtils 业务入口层</li>
 * </ul>
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public final class SecurityContext {

    private SecurityContext() {
    }

    // ==================== 读：可能为 null ====================

    /**
     * 当前会话，未登录返回 null
     */
    public static SessionInfo getSession() {
        return SessionContext.get();
    }

    /**
     * 当前用户，未登录返回 null
     */
    public static SessionUser getUser() {
        return SessionContext.getUser();
    }

    /**
     * 当前用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        return SessionContext.getUserId();
    }

    /**
     * 当前用户名，未登录返回 null
     */
    public static String getUsername() {
        return SessionContext.getUsername();
    }

    /**
     * 当前昵称，未登录返回 null
     */
    public static String getNickname() {
        return SessionContext.getNickname();
    }

    /**
     * 当前角色，未登录返回空 Set
     */
    public static Set<String> getRoles() {
        return SessionContext.getRoles();
    }

    /**
     * 当前权限，未登录返回空 Set
     */
    public static Set<String> getPerms() {
        return SessionContext.getPerms();
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return SessionContext.isLogin();
    }

    /**
     * 是否管理端
     */
    public static boolean isAdmin() {
        return SessionContext.isAdmin();
    }

    /**
     * 是否 C 端
     */
    public static boolean isMember() {
        return SessionContext.isMember();
    }

    // ==================== 读：require（未登录抛 401） ====================

    /**
     * 要求登录，未登录抛 {@link UnauthorizedException}
     */
    public static SessionInfo requireLogin() {
        SessionInfo info = SessionContext.get();
        if (info == null) {
            throw UnauthorizedException.notLogin();
        }
        return info;
    }

    /**
     * 要求登录并返回用户
     */
    public static SessionUser requireUser() {
        SessionUser user = SessionContext.getUser();
        if (user == null) {
            throw UnauthorizedException.notLogin();
        }
        return user;
    }

    /**
     * 要求登录并返回用户 ID
     */
    public static Long requireUserId() {
        return requireUser().getUserId();
    }

    /**
     * 要求登录并返回用户名
     */
    public static String requireUsername() {
        return requireUser().getUsername();
    }

}
