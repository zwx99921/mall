package com.we.mall.common.session.builder;

import com.we.mall.common.session.properties.SessionProperties;

/**
 * Session Key 生成器
 * <p>
 * session:{client}:{sessionId}
 * session:{client}:user:{userId}:{device}
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SessionKeyBuilder {

    private SessionKeyBuilder() {
    }

    public static String sessionKey(SessionProperties props, String clientCode, String sessionId) {
        return props.getClient(clientCode).getKeyPrefix() + sessionId;
    }

    public static String userDeviceKey(SessionProperties props, String clientCode,
                                       Long userId, String device) {
        return props.getClient(clientCode).getKeyPrefix() + "user:" + userId + ":" + device;
    }

    public static String userPattern(SessionProperties props, String clientCode, Long userId) {
        return props.getClient(clientCode).getKeyPrefix() + "user:" + userId + ":*";
    }

    public static String clientPattern(SessionProperties props, String clientCode) {
        return props.getClient(clientCode).getKeyPrefix() + "*";
    }

}
