package com.we.mall.common.session.store;

import java.util.List;
import java.util.Set;

/**
 * 索引存储
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public interface SessionIndexStore {

    /**
     * 加索引
     */
    void add(String clientCode, Long userId, String deviceType, String sessionId);

    /**
     * 删索引
     */
    void remove(String clientCode, Long userId, String deviceType, String sessionId);

    /**
     * 删某用户的所有索引
     */
    void removeUser(String clientCode, Long userId);

    /**
     * 查某用户某设备的所有 sessionId
     */
    List<String> listByUserAndDevice(String clientCode, Long userId, String deviceType);

    /**
     * 查某用户所有 sessionId（跨设备）
     */
    List<String> listByUser(String clientCode, Long userId);

    /**
     * 查某用户的所有设备类型
     */
    Set<String> listDeviceTypes(String clientCode, Long userId);

}
