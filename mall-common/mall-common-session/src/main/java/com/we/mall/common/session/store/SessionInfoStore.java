package com.we.mall.common.session.store;

import com.we.mall.common.session.model.SessionInfo;

/**
 * 信息存储
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface SessionInfoStore {

    /**
     * 保存会话
     */
    void save(SessionInfo info, long timeoutSeconds);

    /**
     * 读取会话
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
     * 获取 TTL（秒），-1 永久，-2 不存在
     */
    Long getTtl(String clientCode, String sessionId);

}
