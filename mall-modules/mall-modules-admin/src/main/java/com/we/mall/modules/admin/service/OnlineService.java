package com.we.mall.modules.admin.service;

import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.modules.admin.model.request.OnlinePageRequest;
import com.we.mall.modules.admin.model.response.OnlineSessionResponse;
import com.we.mall.modules.admin.model.response.OnlineUserResponse;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public interface OnlineService {

    /**
     * 在线用户分页（按 userId 分组）
     */
    PageResult<OnlineUserResponse> page(OnlinePageRequest request);

    /**
     * 某用户的在线设备列表
     */
    List<OnlineSessionResponse> sessions(Long userId);

    /**
     * 踢某用户所有会话
     */
    void kick(Long userId);

    /**
     * 踢指定 session
     */
    void kickSession(String sessionId);

}
