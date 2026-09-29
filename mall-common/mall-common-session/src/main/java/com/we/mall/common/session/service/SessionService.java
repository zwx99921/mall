package com.we.mall.common.session.service;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.core.enums.DeviceType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;

import java.util.List;
import java.util.Set;

/**
 * 会话服务
 * <p>
 * 只负责会话的存、取、踢。不解析 token，不做权限
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface SessionService {

    /**
     * 创建会话
     *
     * @param clientType  MEMBER / ADMIN
     * @param sessionUser 用户信息
     * @param loginIp     登录 IP
     * @param userAgent   UA
     * @param deviceType  设备类型：PC / H5 / APP / MINI
     * @return sessionId
     */
    String create(ClientType clientType, SessionUser sessionUser,
                  String loginIp, String userAgent, DeviceType deviceType, String deviceName);

    /**
     * 读取会话
     */
    SessionInfo get(ClientType clientType, String sessionId);

    /**
     * 读取会话并刷新 TTL（滑动过期）
     */
    SessionInfo getAndRefresh(ClientType clientType, String sessionId);

    /**
     * 销毁会话
     */
    void destroy(ClientType clientType, String sessionId);

    /**
     * 踢某端某设备的全部会话
     */
    void kickByUserAndDevice(ClientType clientType, Long userId, String deviceType);

    /**
     * 踢某端该用户所有会话
     */
    void kickAll(ClientType clientType, Long userId);

    /**
     * 查某端某设备所有会话
     */
    List<SessionInfo> listByUserAndDevice(ClientType clientType, Long userId, String deviceType);

    /**
     * 查某端该用户所有会话（跨设备）
     */
    List<SessionInfo> listByUser(ClientType clientType, Long userId);

    /**
     * 查某端该用户所有设备类型
     */
    Set<String> listDeviceTypes(ClientType clientType, Long userId);

    // ==================== SessionUser ====================

    /**
     * 获取会话用户
     */
    SessionUser getSessionUser(ClientType clientType, Long userId);

    /**
     * 保存会话用户
     */
    void saveSessionUser(ClientType clientType, Long userId, SessionUser sessionUser);

    /**
     * 移除会话用户
     */
    void removeSessionUser(ClientType clientType, Long userId);

    /**
     * 刷新 SessionUser 的 roles / perms
     * <p>
     * 保留原 TTL。SessionUser 不存在时跳过。
     *
     * @param clientType 端类型
     * @param userId     用户ID
     * @param roles      新角色
     * @param perms      新权限
     */
    void refreshAuth(ClientType clientType, Long userId, Set<String> roles, Set<String> perms);

    /**
     * 刷新用户所有在线会话的 SessionUser（保留原 TTL）
     */
    void refreshSessionUser(ClientType clientType, Long userId, SessionUser sessionUser);

}
