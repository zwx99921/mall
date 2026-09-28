package com.we.mall.common.session.builder;

import com.we.mall.common.session.properties.SessionProperties;

/**
 * Session Key 生成器
 * <p>
 * 会话:{prefix}info:{sessionId}
 * 索引:{prefix}idx:{userId}:{device}
 * 用户:{prefix}user:{userId}
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SessionKeyBuilder {

    private SessionKeyBuilder() {
    }

    // ==================== 会话 ====================

    /**
     * 信息 key：{prefix}info:{sessionId}
     */
    public static String sessionKey(SessionProperties props, String clientCode, String sessionId) {
        return props.getClient(clientCode).getKeyPrefix() + "info:" + sessionId;
    }

    // ==================== 索引 ====================

    /**
     * 索引 key：{prefix}idx:{userId}
     */
    public static String indexKey(SessionProperties props, String clientCode, Long userId) {
        return props.getClient(clientCode).getKeyPrefix() + "idx:" + userId;
    }

    // ==================== 用户 ====================

    /**
     * 用户 key：{prefix}user:{userId}
     */
    public static String userKey(SessionProperties props, String clientCode, Long userId) {
        return props.getClient(clientCode).getKeyPrefix() + "user:" + userId;
    }

    // ==================== pattern ====================

    public static String clientPattern(SessionProperties props, String clientCode) {
        return props.getClient(clientCode).getKeyPrefix() + "*";
    }

    public static String clientSessionPattern(SessionProperties props, String clientCode) {
        return props.getClient(clientCode).getKeyPrefix() + "info:*";
    }

    public static String clientIndexPattern(SessionProperties props, String clientCode) {
        return props.getClient(clientCode).getKeyPrefix() + "idx:*";
    }

    public static String clientUserPattern(SessionProperties props, String clientCode) {
        return props.getClient(clientCode).getKeyPrefix() + "user:*";
    }

}
