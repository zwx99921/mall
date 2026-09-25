package com.we.mall.common.session.service.impl;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.common.session.store.SessionStore;
import com.we.mall.common.session.util.SessionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 会话服务实现
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSessionServiceImpl implements SessionService {

    private final SessionStore sessionStore;

    private final SessionProperties sessionProperties;

    @Override
    public String create(ClientType clientType, SessionUser sessionUser,
                         Set<String> roles, Set<String> perms,
                         String loginIp, String userAgent, String deviceType, String deviceName) {

        SessionProperties.ClientConfig cfg = sessionProperties.getClient(clientType.getCode());

        // 设备类型校验
        if (!cfg.getDeviceTypes().contains(deviceType) && !cfg.isAllowUnknownDevice()) {
            throw BusinessException.of(ResultCode.PARAM_FORMAT_ERROR, "[" + clientType.getCode() + "] 不支持的设备类型: " + deviceType);
        }

        // 同端互踢
        if (cfg.isSameDeviceKick()) {
            kickByUserAndDevice(clientType, sessionUser.getUserId(), deviceType);
        }

        // 超量踢最早的
        if (cfg.getMaxPerDevice() > 0) {
            enforceMaxPerDevice(clientType, sessionUser.getUserId(), deviceType, cfg.getMaxPerDevice());
        }

        // 创建
        String sessionId = SessionIdGenerator.generate(sessionProperties.getSessionIdLength());
        LocalDateTime now = LocalDateTime.now();

        SessionInfo info = SessionInfo.builder()
                .sessionId(sessionId)
                .clientType(clientType.getCode())
                .deviceType(deviceType)
                .deviceName(deviceName)
                .user(sessionUser)
                .loginIp(loginIp)
                .userAgent(userAgent)
                .createTime(now)
                .lastAccessTime(now)
                .expireTime(now.plusSeconds(sessionProperties.getTimeoutSeconds()))
                .roles(roles)
                .perms(perms)
                .build();

        sessionStore.save(info, sessionProperties.getTimeoutSeconds());

        log.debug("session created [{}] {} for user {} on {}", clientType.getCode(), sessionId, sessionUser.getUserId(), deviceType);

        return sessionId;
    }

    @Override
    public SessionInfo get(ClientType clientType, String sessionId) {
        return sessionStore.get(clientType.getCode(), sessionId);
    }

    @Override
    public SessionInfo getAndRefresh(ClientType clientType, String sessionId) {
        SessionInfo info = sessionStore.get(clientType.getCode(), sessionId);
        if (info == null) {
            return null;
        }
        if (sessionProperties.isSlidingExpiration()) {
            LocalDateTime now = LocalDateTime.now();
            info.setLastAccessTime(now);
            info.setExpireTime(now.plusSeconds(sessionProperties.getTimeoutSeconds()));
            sessionStore.save(info, sessionProperties.getTimeoutSeconds());
        }
        return info;
    }

    @Override
    public void destroy(ClientType clientType, String sessionId) {
        sessionStore.remove(clientType.getCode(), sessionId);
    }

    @Override
    public void kickByUserAndDevice(ClientType clientType, Long userId, String deviceType) {
        List<String> ids = sessionStore.listByUserAndDevice(clientType.getCode(), userId, deviceType);
        for (String id : ids) {
            sessionStore.remove(clientType.getCode(), id);
        }
        log.info("kicked {} sessions [{}] user={} device={}", ids.size(), clientType.getCode(), userId, deviceType);
    }

    @Override
    public void kickAll(ClientType clientType, Long userId) {
        List<String> ids = sessionStore.listByUser(clientType.getCode(), userId);
        for (String id : ids) {
            sessionStore.remove(clientType.getCode(), id);
        }
        log.info("kicked all {} sessions [{}] user={}", ids.size(), clientType.getCode(), userId);
    }

    @Override
    public List<SessionInfo> listByUserAndDevice(ClientType clientType, Long userId, String deviceType) {
        List<String> ids = sessionStore.listByUserAndDevice(clientType.getCode(), userId, deviceType);
        List<SessionInfo> result = new ArrayList<>(ids.size());
        for (String id : ids) {
            SessionInfo info = sessionStore.get(clientType.getCode(), id);
            if (info != null) {
                result.add(info);
            }
        }
        return result;
    }

    @Override
    public List<SessionInfo> listByUser(ClientType clientType, Long userId) {
        List<String> ids = sessionStore.listByUser(clientType.getCode(), userId);
        List<SessionInfo> result = new ArrayList<>(ids.size());
        for (String id : ids) {
            SessionInfo info = sessionStore.get(clientType.getCode(), id);
            if (info != null) {
                result.add(info);
            }
        }
        return result;
    }

    @Override
    public Set<String> listDeviceTypes(ClientType clientType, Long userId) {
        return sessionStore.listDeviceTypes(clientType.getCode(), userId);
    }

    /**
     * 超出每端上限时，踢掉最早的会话
     */
    private void enforceMaxPerDevice(ClientType clientType, Long userId, String device, int max) {
        List<String> ids = sessionStore.listByUserAndDevice(clientType.getCode(), userId, device);
        if (ids.size() < max) {
            return;
        }
        // 按创建时间升序，踢最早的
        ids.sort(Comparator.comparing(id -> {
            SessionInfo i = sessionStore.get(clientType.getCode(), id);
            return i == null ? LocalDateTime.MIN : i.getCreateTime();
        }));
        int needKick = ids.size() - max + 1;
        for (int i = 0; i < needKick; i++) {
            sessionStore.remove(clientType.getCode(), ids.get(i));
        }
        log.info("enforceMaxPerDevice kicked {} sessions [{}] user={} device={}", needKick, clientType.getCode(), userId, device);
    }

}
