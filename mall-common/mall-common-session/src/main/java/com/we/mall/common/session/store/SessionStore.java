package com.we.mall.common.session.store;

import com.we.mall.common.session.model.SessionInfo;

import java.util.List;
import java.util.Set;

/**
 * 会话存储抽象
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface SessionStore {

    /**
     * 保存会话
     */
    void save(SessionInfo info, long timeoutSeconds);

    /**
     * 根据 client + sessionId 读取
     */
    SessionInfo get(String clientCode, String sessionId);

    /**
     * 删除会话
     */
    void remove(String clientCode, String sessionId);

    /**
     * 刷新 TTL
     */
    void refresh(String clientCode, String sessionId, long timeoutSeconds);

    /**
     * 查某端某用户某设备的所有 sessionId
     */
    List<String> listByUserAndDevice(String clientCode, Long userId, String deviceType);

    /**
     * 查某端某用户所有 sessionId
     */
    List<String> listByUser(String clientCode, Long userId);

    /**
     * 查某端某用户的所有设备类型
     */
    Set<String> listDeviceTypes(String clientCode, Long userId);

    /**
     * 加索引
     */
    void addUserDeviceSession(String clientCode, Long userId, String deviceType, String sessionId);

    /**
     * 去索引
     */
    void removeUserDeviceSession(String clientCode, Long userId, String deviceType, String sessionId);

}
