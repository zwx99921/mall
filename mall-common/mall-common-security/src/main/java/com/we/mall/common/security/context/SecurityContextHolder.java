package com.we.mall.common.security.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

/**
 * 安全上下文持有者
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public final class SecurityContextHolder {

    private static final TransmittableThreadLocal<SessionContext> HOLDER = new TransmittableThreadLocal<>();

    private SecurityContextHolder() {
    }

    // ==================== Context ====================

    public static SessionContext getContext() {
        return HOLDER.get();
    }

    public static void setContext(SessionContext context) {
        HOLDER.set(context);
    }

    public static void clearContext() {
        HOLDER.remove();
    }

    // ==================== 便捷 ====================

    public static SessionInfo getSession() {
        SessionContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getSession();
    }

    public static void setSession(SessionInfo session) {
        SessionContext ctx = HOLDER.get();
        if (ctx == null) {
            ctx = new SessionContext();
            HOLDER.set(ctx);
        }
        ctx.setSession(session);
    }

    public static SessionUser getUser() {
        SessionContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getUser();
    }

    public static void setUser(SessionUser user) {
        SessionContext ctx = HOLDER.get();
        if (ctx == null) {
            ctx = new SessionContext();
            HOLDER.set(ctx);
        }
        ctx.setUser(user);
    }

}
