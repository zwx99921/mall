package com.we.mall.common.session.store;

import com.we.mall.common.session.model.SessionUser;

/**
 * 用户存储
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public interface SessionUserStore {

    /**
     * 保存用户
     */
    void save(String clientCode, Long userId, SessionUser sessionUser, long timeoutSeconds);

    /**
     * 读取用户
     */
    SessionUser get(String clientCode, Long userId);

    /**
     * 删除用户
     */
    void remove(String clientCode, Long userId);

    /**
     * 刷新 TTL
     */
    void refresh(String clientCode, Long userId, long timeoutSeconds);

    /**
     * 获取 TTL（秒）
     */
    Long getTtl(String clientCode, Long userId);

}
