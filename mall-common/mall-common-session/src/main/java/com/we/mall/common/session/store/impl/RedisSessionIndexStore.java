package com.we.mall.common.session.store.impl;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisSetOpsService;
import com.we.mall.common.session.builder.SessionKeyBuilder;
import com.we.mall.common.session.model.SessionIndex;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.store.SessionIndexStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 索引存储实现
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSessionIndexStore implements SessionIndexStore {

    private final RedisSetOpsService redisSetOpsService;
    private final RedisKeyOpsService redisKeyOpsService;
    private final SessionProperties sessionProperties;

    // ==================== 加 / 删 ====================

    @Override
    public void add(String clientCode, Long userId, String deviceType, String sessionId) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        SessionIndex index = new SessionIndex(deviceType, sessionId);
        redisSetOpsService.add(key, index);
        redisKeyOpsService.expire(key, sessionProperties.getTimeoutSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void remove(String clientCode, Long userId, String deviceType, String sessionId) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        SessionIndex index = new SessionIndex(deviceType, sessionId);
        redisSetOpsService.remove(key, index);
    }

    @Override
    public void removeUser(String clientCode, Long userId) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        redisKeyOpsService.delete(key);
    }

    // ==================== 查 ====================

    @Override
    public List<String> listByUserAndDevice(String clientCode, Long userId, String deviceType) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        Set<SessionIndex> all = redisSetOpsService.members(key, SessionIndex.class);
        return all.stream()
                .filter(e -> deviceType.equals(e.getDeviceType()))
                .map(SessionIndex::getSessionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> listByUser(String clientCode, Long userId) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        Set<SessionIndex> all = redisSetOpsService.members(key, SessionIndex.class);
        return all.stream()
                .map(SessionIndex::getSessionId)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> listDeviceTypes(String clientCode, Long userId) {
        String key = SessionKeyBuilder.indexKey(sessionProperties, clientCode, userId);
        Set<SessionIndex> all = redisSetOpsService.members(key, SessionIndex.class);
        return all.stream()
                .map(SessionIndex::getDeviceType)
                .collect(Collectors.toSet());
    }

}
