package com.we.mall.common.session.store.impl;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisSetOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.session.builder.SessionKeyBuilder;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.store.SessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 会话存储实现
 * <p>
 * 数据结构：
 * - session:{client}:{sessionId}              String  会话信息（JSON）
 * - session:{client}:user:{userId}:{device}   Set     该用户该设备的所有 sessionId
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSessionStoreImpl implements SessionStore {

    private final RedisStringOpsService redisStringOpsService;
    private final RedisSetOpsService redisSetOpsService;
    private final RedisKeyOpsService redisKeyOpsService;
    private final SessionProperties sessionProperties;

    @Override
    public void save(SessionInfo info, long timeoutSeconds) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, info.getClientType(), info.getSessionId());
        redisStringOpsService.set(key, info, timeoutSeconds, TimeUnit.SECONDS);
        addUserDeviceSession(info.getClientType(),
                info.getUser().getUserId(),
                info.getDeviceType(),
                info.getSessionId());
    }

    @Override
    public SessionInfo get(String clientCode, String sessionId) {
        String key = SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId);
        return redisStringOpsService.get(key, SessionInfo.class);
    }

    @Override
    public void remove(String clientCode, String sessionId) {
        SessionInfo info = get(clientCode, sessionId);
        redisKeyOpsService.delete(SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId));
        if (info != null && info.getUser() != null) {
            removeUserDeviceSession(clientCode,
                    info.getUser().getUserId(),
                    info.getDeviceType(),
                    sessionId);
        }
    }

    @Override
    public void refresh(String clientCode, String sessionId, long timeoutSeconds) {
        redisKeyOpsService.expire(SessionKeyBuilder.sessionKey(sessionProperties, clientCode, sessionId), timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public List<String> listByUserAndDevice(String clientCode, Long userId, String deviceType) {
        String key = SessionKeyBuilder.userDeviceKey(sessionProperties, clientCode, userId, deviceType);
        Set<String> ids = redisSetOpsService.members(key, String.class);
        return new ArrayList<>(ids);
    }

    @Override
    public List<String> listByUser(String clientCode, Long userId) {
        Set<String> devices = listDeviceTypes(clientCode, userId);
        List<String> result = new ArrayList<>();
        for (String d : devices) {
            result.addAll(listByUserAndDevice(clientCode, userId, d));
        }
        return result;
    }

    @Override
    public Set<String> listDeviceTypes(String clientCode, Long userId) {
        String pattern = SessionKeyBuilder.userPattern(sessionProperties, clientCode, userId);
        Set<String> keys = redisKeyOpsService.scan(pattern, 100);
        Set<String> devices = new HashSet<>();
        String prefix = sessionProperties.getClient(clientCode).getKeyPrefix()
                + "user:" + userId + ":";
        for (String k : keys) {
            devices.add(k.substring(prefix.length()));
        }
        return devices;
    }

    @Override
    public void addUserDeviceSession(String clientCode, Long userId, String deviceType, String sessionId) {
        String key = SessionKeyBuilder.userDeviceKey(sessionProperties, clientCode, userId, deviceType);
        redisSetOpsService.add(key, sessionId);
        redisKeyOpsService.expire(key, sessionProperties.getTimeoutSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void removeUserDeviceSession(String clientCode, Long userId, String deviceType, String sessionId) {
        String key = SessionKeyBuilder.userDeviceKey(sessionProperties, clientCode, userId, deviceType);
        redisSetOpsService.remove(key, sessionId);
    }

}
