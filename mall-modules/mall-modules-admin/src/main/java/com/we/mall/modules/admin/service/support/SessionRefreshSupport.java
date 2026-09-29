package com.we.mall.modules.admin.service.support;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.modules.admin.mapper.MenuMapper;
import com.we.mall.modules.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 会话授权刷新辅助
 * <p>
 * 只负责"查 roles / perms"，刷新动作委托给 SessionService.refreshAuth。
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionRefreshSupport {

    private static final ClientType CLIENT = ClientType.ADMIN;

    private final SessionService sessionService;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    /**
     * 刷新某个用户的授权
     */
    public void refresh(Long userId) {
        if (userId == null) {
            return;
        }
        Set<String> roles = roleMapper.selectRoleCodesByUserId(userId);
        Set<String> perms = menuMapper.selectPermsByUserId(userId);
        sessionService.refreshAuth(CLIENT, userId, roles, perms);
    }

    /**
     * 批量刷新（去重）
     */
    public void refresh(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        Set<Long> distinct = new HashSet<>(userIds);
        for (Long userId : distinct) {
            refresh(userId);
        }
    }

}
