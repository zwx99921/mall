package com.we.mall.common.session.service.impl;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.core.enums.DeviceType;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.common.session.store.SessionIndexStore;
import com.we.mall.common.session.store.SessionInfoStore;
import com.we.mall.common.session.store.SessionUserStore;
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
public class RedisSessionService implements SessionService {

    private final SessionInfoStore sessionInfoStore;
    private final SessionIndexStore sessionIndexStore;
    private final SessionUserStore sessionUserStore;
    private final SessionProperties sessionProperties;


    // ==================== 创建 ====================

    @Override
    public String create(ClientType clientType, SessionUser sessionUser,
                         String loginIp, String userAgent, DeviceType deviceType, String deviceName) {

        SessionProperties.ClientConfig cfg = sessionProperties.getClient(clientType.getCode());

        // 设备类型校验
        if (!cfg.getDeviceTypes().contains(deviceType) && !cfg.isAllowUnknownDevice()) {
            throw BusinessException.of(ResultCode.CLIENT_TYPE_NOT_SUPPORT, "");
        }

        // 同端互踢
        if (cfg.isSameDeviceKick()) {
            kickByUserAndDevice(clientType, sessionUser.getUserId(), deviceType.name());
        }

        // 超量踢最早的
        if (cfg.getMaxPerDevice() > 0) {
            enforceMaxPerDevice(clientType.getCode(), sessionUser.getUserId(), deviceType.name(), cfg.getMaxPerDevice());
        }

        // 创建
        String sessionId = SessionIdGenerator.generate(sessionProperties.getSessionIdLength());
        LocalDateTime now = LocalDateTime.now();
        long ttl = sessionProperties.getTimeoutSeconds();

        // 1. 会话
        SessionInfo info = SessionInfo.builder()
                .sessionId(sessionId)
                .clientType(clientType.getCode())
                .deviceType(deviceType.name())
                .deviceName(deviceName)
                .userId(sessionUser.getUserId())
                .loginIp(loginIp)
                .userAgent(userAgent)
                .createTime(now)
                .lastAccessTime(now)
                .expireTime(now.plusSeconds(ttl))
                .build();
        sessionInfoStore.save(info, ttl);

        // 2. 索引
        sessionIndexStore.add(clientType.getCode(), sessionUser.getUserId(), deviceType.name(), sessionId);

        // 3. 用户
        sessionUserStore.save(clientType.getCode(), sessionUser.getUserId(), sessionUser, ttl);

        log.debug("session created [{}] {} for user {} on {}", clientType.getCode(), sessionId, sessionUser.getUserId(), deviceType);

        return sessionId;
    }

    // ==================== 读取 ====================

    @Override
    public SessionInfo get(ClientType clientType, String sessionId) {
        return sessionInfoStore.get(clientType.getCode(), sessionId);
    }

    @Override
    public SessionInfo getAndRefresh(ClientType clientType, String sessionId) {
        SessionInfo info = sessionInfoStore.get(clientType.getCode(), sessionId);
        if (info == null) {
            return null;
        }
        if (sessionProperties.isSlidingExpiration()) {
            LocalDateTime now = LocalDateTime.now();
            long ttl = sessionProperties.getTimeoutSeconds();

            info.setLastAccessTime(now);
            info.setExpireTime(now.plusSeconds(ttl));

            sessionInfoStore.save(info, ttl);
            sessionIndexStore.add(clientType.getCode(), info.getUserId(), info.getDeviceType(), info.getSessionId());
            sessionUserStore.refresh(clientType.getCode(), info.getUserId(), ttl);
        }
        return info;
    }

    // ==================== 销毁 ====================

    @Override
    public void destroy(ClientType clientType, String sessionId) {
        SessionInfo info = sessionInfoStore.get(clientType.getCode(), sessionId);
        if (info == null) {
            return;
        }

        sessionInfoStore.remove(clientType.getCode(), sessionId);
        sessionIndexStore.remove(clientType.getCode(), info.getUserId(), info.getDeviceType(), sessionId);

        List<SessionInfo> others = listByUser(clientType, info.getUserId());
        if (others == null || others.isEmpty()) {
            sessionUserStore.remove(clientType.getCode(), info.getUserId());
        }
    }

    @Override
    public void kickByUserAndDevice(ClientType clientType, Long userId, String deviceType) {
        List<String> ids = sessionIndexStore.listByUserAndDevice(clientType.getCode(), userId, deviceType);
        for (String id : ids) {
            sessionInfoStore.remove(clientType.getCode(), id);
            sessionIndexStore.remove(clientType.getCode(), userId, deviceType, id);
        }
        log.info("kicked {} sessions [{}] user={} device={}", ids.size(), clientType, userId, deviceType);
    }

    @Override
    public void kickAll(ClientType clientType, Long userId) {
        List<String> ids = sessionIndexStore.listByUser(clientType.getCode(), userId);

        for (String id : ids) {
            sessionInfoStore.remove(clientType.getCode(), id);
        }
        sessionIndexStore.removeUser(clientType.getCode(), userId);
        sessionUserStore.remove(clientType.getCode(), userId);

        log.info("kicked all {} sessions [{}] user={}", ids.size(), clientType, userId);
    }

    // ==================== 查询 ====================

    @Override
    public List<SessionInfo> listByUserAndDevice(ClientType clientType, Long userId, String deviceType) {
        List<String> ids = sessionIndexStore.listByUserAndDevice(clientType.getCode(), userId, deviceType);
        return loadSessions(clientType.getCode(), ids);
    }

    // ==================== 查询 ====================

    @Override
    public List<SessionInfo> listByUser(ClientType clientType, Long userId) {
        List<String> ids = sessionIndexStore.listByUser(clientType.getCode(), userId);
        return loadSessions(clientType.getCode(), ids);
    }

    @Override
    public Set<String> listDeviceTypes(ClientType clientType, Long userId) {
        return sessionIndexStore.listDeviceTypes(clientType.getCode(), userId);
    }

    // ==================== SessionUser ====================

    @Override
    public SessionUser getSessionUser(ClientType clientType, Long userId) {
        return sessionUserStore.get(clientType.getCode(), userId);
    }

    @Override
    public void saveSessionUser(ClientType clientType, Long userId, SessionUser sessionUser) {
        sessionUserStore.save(clientType.getCode(), userId, sessionUser, sessionProperties.getTimeoutSeconds());
    }

    @Override
    public void removeSessionUser(ClientType clientType, Long userId) {
        sessionUserStore.remove(clientType.getCode(), userId);
    }

    @Override
    public void refreshAuth(ClientType clientType, Long userId, Set<String> roles, Set<String> perms) {
        if (userId == null) {
            return;
        }
        SessionUser user = sessionUserStore.get(clientType.getCode(), userId);
        if (user == null) {
            log.debug("SessionUser 不存在，跳过刷新: clientType={}, userId={}", clientType.getCode(), userId);
            return;
        }
        user.setRoles(roles);
        user.setPerms(perms);
        sessionUserStore.save(clientType.getCode(), userId, user, sessionProperties.getTimeoutSeconds());

        log.info("刷新 SessionUser: clientType={}, userId={}", clientType.getCode(), userId);
    }

    @Override
    public void refreshSessionUser(ClientType clientType, Long userId, SessionUser sessionUser) {
        long ttl = sessionProperties.getTimeoutSeconds();
        sessionUserStore.save(clientType.getCode(), userId, sessionUser, ttl);
        log.info("refreshed sessionUser [{}] user={}", clientType, userId);
    }

    // ==================== 私有辅助 ====================

    /**
     * 加载会话数据
     */
    private List<SessionInfo> loadSessions(String clientCode, List<String> ids) {
        List<SessionInfo> result = new ArrayList<>(ids.size());
        for (String id : ids) {
            SessionInfo info = sessionInfoStore.get(clientCode, id);
            if (info != null) {
                result.add(info);
            }
        }
        return result;
    }

    /**
     * 超出每端上限时，踢掉最早的会话
     */
    private void enforceMaxPerDevice(String clientType, Long userId, String device, int max) {
        List<String> ids = sessionIndexStore.listByUserAndDevice(clientType, userId, device);
        if (ids.size() < max) {
            return;
        }
        ids.sort(Comparator.comparing(id -> {
            SessionInfo i = sessionInfoStore.get(clientType, id);
            return i == null ? LocalDateTime.MIN : i.getCreateTime();
        }));
        int needKick = ids.size() - max + 1;
        for (int i = 0; i < needKick; i++) {
            sessionInfoStore.remove(clientType, ids.get(i));
            sessionIndexStore.remove(clientType, userId, device, ids.get(i));
        }
        log.info("enforceMaxPerDevice kicked {} sessions [{}] user={} device={}", needKick, clientType, userId, device);
    }

}
