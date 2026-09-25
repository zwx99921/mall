package com.we.mall.common.redis.constant.key;

/**
 * 缓存 key 规范
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public final class RedisKeys {

    /**
     * 全局前缀
     */
    public static final String PREFIX = "mall:";
    /**
     * 会话模块
     */
    public static final String SESSION = PREFIX + "session:";
    /**
     * 认证模块
     */
    public static final String AUTH = PREFIX + "auth:";
    /**
     * 缓存模块
     */
    public static final String CACHE = PREFIX + "cache:";
    /**
     * 锁模块
     */
    public static final String LOCK = PREFIX + "lock:";
    /**
     * 限流模块
     */
    public static final String RATE_LIMIT = PREFIX + "rate:";

    private RedisKeys() {
    }

    /**
     * 构建 key
     */
    public static String build(String prefix, Object... parts) {
        return prefix + join(parts);
    }

    public static String session(String biz, Object... parts) {
        return build(SESSION + biz + ":", parts);
    }

    public static String auth(String biz, Object... parts) {
        return build(AUTH + biz + ":", parts);
    }

    public static String cache(String entity, Object... parts) {
        return build(CACHE + entity + ":", parts);
    }

    public static String lock(String biz, Object... parts) {
        return build(LOCK + biz + ":", parts);
    }

    public static String rateLimit(String biz, Object... parts) {
        return build(RATE_LIMIT + biz + ":", parts);
    }

    private static String join(Object... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

}
