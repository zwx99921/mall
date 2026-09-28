package com.we.mall.common.session.store.impl;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.session.builder.SessionKeyBuilder;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.store.SessionInfoStore;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Redis 会话存储实现
 * <p>
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisSessionInfoStore implements SessionInfoStore {

    private final RedisStringOpsService redisStringOpsService;
    private final RedisKeyOpsService redisKeyOpsService;
    private final SessionProperties sessionProperties;

    @Override
    public void save(SessionInfo info, long timeoutSeconds) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, info.getClientType(), info.getSessionId());
        redisStringOpsService.set(key, info, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public SessionInfo get(String clientCode, String sessionId) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId);
        return redisStringOpsService.get(key, SessionInfo.class);
    }

    @Override
    public void remove(String clientCode, String sessionId) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId);
        redisKeyOpsService.delete(key);
    }

    @Override
    public void refresh(String clientCode, String sessionId, long timeoutSeconds) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId);
        redisKeyOpsService.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Long getTtl(String clientCode, String sessionId) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId);
        return redisKeyOpsService.getExpire(key, TimeUnit.SECONDS);
    }
}
