package com.we.mall.common.security.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 安全上下文持有者
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public final class SecurityContextHolder {

    private static final TransmittableThreadLocal<SecurityContext> HOLDER = new TransmittableThreadLocal<>();

    private SecurityContextHolder() {
    }

    // ==================== Context ====================

    public static SecurityContext getContext() {
        return HOLDER.get();
    }

    public static void setContext(SecurityContext context) {
        HOLDER.set(context);
    }

    public static void clearContext() {
        HOLDER.remove();
    }

}
