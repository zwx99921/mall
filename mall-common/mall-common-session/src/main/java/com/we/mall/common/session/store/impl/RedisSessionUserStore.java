package com.we.mall.common.session.store.impl;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.session.builder.SessionKeyBuilder;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.store.SessionUserStore;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Redis 用户存储实现
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@RequiredArgsConstructor
public class RedisSessionUserStore implements SessionUserStore {

    private final RedisStringOpsService redisStringOpsService;
    private final RedisKeyOpsService redisKeyOpsService;
    private final SessionProperties sessionProperties;

    @Override
    public void save(String clientCode, Long userId, SessionUser sessionUser, long timeoutSeconds) {
        String key = SessionKeyBuilder.userKey(sessionProperties, clientCode, userId);
        redisStringOpsService.set(key, sessionUser, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public SessionUser get(String clientCode, Long userId) {
        String key = SessionKeyBuilder.userKey(sessionProperties, clientCode, userId);
        return redisStringOpsService.get(key, SessionUser.class);
    }

    @Override
    public void remove(String clientCode, Long userId) {
        String key = SessionKeyBuilder.userKey(sessionProperties, clientCode, userId);
        redisKeyOpsService.delete(key);
    }

    @Override
    public void refresh(String clientCode, Long userId, long timeoutSeconds) {
        String key = SessionKeyBuilder.userKey(sessionProperties, clientCode, userId);
        redisKeyOpsService.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Long getTtl(String clientCode, Long userId) {
        String key = SessionKeyBuilder.userKey(sessionProperties, clientCode, userId);
        return redisKeyOpsService.getExpire(key, TimeUnit.SECONDS);
    }
}
