package com.we.mall.modules.admin.service.impl;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.security.util.SecurityUtils;
import com.we.mall.common.session.builder.SessionKeyBuilder;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.modules.admin.model.request.OnlinePageRequest;
import com.we.mall.modules.admin.model.response.OnlineSessionResponse;
import com.we.mall.modules.admin.model.response.OnlineUserResponse;
import com.we.mall.modules.admin.service.OnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现
 * <p>
 * 数据源：scan mall:session:admin:*，过滤掉索引 key（含 :user:），
 * 批量读 SessionInfo，按 userId 分组。
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    private static final ClientType CLIENT = ClientType.ADMIN;

    /**
     * scan 每次扫描建议数量
     */
    private static final long SCAN_COUNT = 1000L;

    private final RedisKeyOpsService redisKeyOpsService;
    private final RedisStringOpsService redisStringOpsService;
    private final SessionService sessionService;
    private final SessionProperties sessionProperties;

    // ==================== 分页 ====================

    @Override
    public PageResult<OnlineUserResponse> page(OnlinePageRequest request) {
        // 1. 拉全部在线 session
        List<SessionInfo> all = listAllSessions();

        // 2. 按 userId 分组
        Map<Long, List<SessionInfo>> grouped = all.stream()
                .filter(info -> info.getUserId() != null)
                .collect(Collectors.groupingBy(
                        SessionInfo::getUserId,
                        LinkedHashMap::new,
                        Collectors.toList()));

        // 3. 组装 + 过滤
        List<OnlineUserResponse> rows = new ArrayList<>(grouped.size());
        for (Map.Entry<Long, List<SessionInfo>> entry : grouped.entrySet()) {
            OnlineUserResponse row = buildOnlineUser(entry.getKey(), entry.getValue());
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                String uname = row.getUsername();
                if (uname == null
                        || !uname.toLowerCase().contains(request.getUsername().toLowerCase())) {
                    continue;
                }
            }
            rows.add(row);
        }

        // 4. 按最近活跃时间倒序
        rows.sort(Comparator.comparing(
                OnlineUserResponse::getLastAccessTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        // 5. 内存分页
        long total = rows.size();
        long pageNum = request.getPageNum() == null || request.getPageNum() < 1
                ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null || request.getPageSize() < 1
                ? 10L : request.getPageSize();
        long from = (pageNum - 1) * pageSize;
        List<OnlineUserResponse> records;
        if (from >= total) {
            records = Collections.emptyList();
        } else {
            int to = (int) Math.min(from + pageSize, total);
            records = rows.subList((int) from, to);
        }

        long pages = (total + pageSize - 1) / pageSize;

        return PageResult.of(pages, total, pageNum, pageSize, records);
    }

    // ==================== 设备列表 ====================

    @Override
    public List<OnlineSessionResponse> sessions(Long userId) {
        List<SessionInfo> list = sessionService.listByUser(CLIENT, userId);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        // 查一次 SessionUser，复用
        SessionUser user = sessionService.getSessionUser(CLIENT, userId);
        return list.stream()
                .map(info -> toSessionResponse(info, user))
                .collect(Collectors.toList());
    }

    // ==================== 踢 ====================

    @Override
    public void kick(Long userId) {
        Long currentUserId = SecurityUtils.getUserId();
        if (userId != null && userId.equals(currentUserId)) {
            throw BusinessException.of(ResultCode.CANNOT_KICK_SELF);
        }

        List<SessionInfo> list = sessionService.listByUser(CLIENT, userId);
        if (list == null || list.isEmpty()) {
            throw BusinessException.of(ResultCode.ONLINE_USER_NOT_FOUND);
        }

        sessionService.kickAll(CLIENT, userId);
        log.info("kick online user: userId={}", userId);
    }

    @Override
    public void kickSession(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw BusinessException.of(ResultCode.PARAM_FORMAT_ERROR, "sessionId 不能为空");
        }

        SessionInfo info = sessionService.get(CLIENT, sessionId);
        if (info == null) {
            throw BusinessException.of(ResultCode.ONLINE_SESSION_NOT_FOUND);
        }

        String currentSessionId = SecurityUtils.getSessionId();
        if (sessionId.equals(currentSessionId)) {
            throw BusinessException.of(ResultCode.CANNOT_KICK_SELF);
        }

        sessionService.destroy(CLIENT, sessionId);
        log.info("kick online session: sessionId={}", sessionId);
    }

    // ==================== 私有辅助 ====================

    /**
     * 扫描全部在线 session
     */
    private List<SessionInfo> listAllSessions() {
        String pattern = SessionKeyBuilder.clientSessionPattern(sessionProperties, CLIENT.getCode());
        Set<String> keys = redisKeyOpsService.scan(pattern, SCAN_COUNT);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> sessionKeys = new ArrayList<>(keys);
        List<SessionInfo> infos = redisStringOpsService.multiGet(sessionKeys, SessionInfo.class);
        if (infos == null || infos.isEmpty()) {
            return Collections.emptyList();
        }
        return infos.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private OnlineUserResponse buildOnlineUser(Long userId, List<SessionInfo> list) {
        OnlineUserResponse row = new OnlineUserResponse();
        row.setUserId(userId);

        // 从 SessionUser 拿 username / nickname
        SessionUser user = sessionService.getSessionUser(CLIENT, userId);
        if (user != null) {
            row.setUsername(user.getUsername());
            row.setNickname(user.getNickname());
        }

        row.setSessionCount(list.size());

        List<String> devices = list.stream()
                .map(SessionInfo::getDeviceType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        row.setDeviceTypes(devices);

        LocalDateTime latest = list.stream()
                .map(SessionInfo::getLastAccessTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        row.setLastAccessTime(latest);

        return row;
    }

    private OnlineSessionResponse toSessionResponse(SessionInfo info, SessionUser user) {
        OnlineSessionResponse r = new OnlineSessionResponse();
        r.setSessionId(info.getSessionId());
        r.setClientType(info.getClientType());
        r.setDeviceType(info.getDeviceType());
        r.setDeviceName(info.getDeviceName());
        r.setLoginIp(info.getLoginIp());
        r.setUserAgent(info.getUserAgent());
        r.setCreateTime(info.getCreateTime());
        r.setLastAccessTime(info.getLastAccessTime());
        r.setExpireTime(info.getExpireTime());

        r.setUserId(info.getUserId());
        if (user != null) {
            r.setUsername(user.getUsername());
            r.setNickname(user.getNickname());
        }
        return r;
    }

}
