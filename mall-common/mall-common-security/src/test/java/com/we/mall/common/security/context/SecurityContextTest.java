package com.we.mall.common.security.context;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@DisplayName("SecurityContext 测试")
public class SecurityContextTest {

    @BeforeEach
    @AfterEach
    void clean() {
        SessionContext.clear();
    }

    // ==================== 工具方法 ====================
    private SessionInfo buildSession(String clientType, Long userId, Set<String> roles, Set<String> perms) {
        SessionUser user = new SessionUser();
        user.setUserId(userId);
        user.setUsername("user" + userId);
        user.setNickname("昵称" + userId);

        SessionInfo info = new SessionInfo();
        info.setSessionId("sid-" + userId);
        info.setClientType(clientType);
        info.setDeviceType("PC");
        info.setUser(user);
        info.setRoles(roles);
        info.setPerms(perms);
        return info;
    }

    // ==================== isLogin ====================

    @Test
    @DisplayName("未登录：isLogin 返回 false")
    void testNotLogin() {
        assertFalse(SecurityContext.isLogin());
        assertNull(SecurityContext.getSession());
        assertNull(SecurityContext.getUserId());
        assertNull(SecurityContext.getUsername());
    }

    @Test
    @DisplayName("登录后：isLogin 返回 true")
    void testLogin() {
        SessionInfo info = buildSession("admin", 1L, Collections.singleton("admin"), Collections.emptySet());
        SessionContext.set(info);

        assertTrue(SecurityContext.isLogin());
        assertEquals(info, SecurityContext.getSession());
        assertEquals(1L, SecurityContext.getUserId());
        assertEquals("user1", SecurityContext.getUsername());
        assertEquals("昵称1", SecurityContext.getNickname());
    }

    // ==================== getXxx ====================

    @Test
    @DisplayName("未登录：getRoles / getPerms 返回空 Set")
    void testGetRolesPermsNotLogin() {
        assertTrue(SecurityContext.getRoles().isEmpty());
        assertTrue(SecurityContext.getPerms().isEmpty());
    }

    @Test
    @DisplayName("登录后：getRoles / getPerms 返回正确")
    void testGetRolesPerms() {
        Set<String> roles = new LinkedHashSet<>(Arrays.asList("admin", "ops"));
        Set<String> perms = new LinkedHashSet<>(Arrays.asList("user:read", "user:write"));

        SessionContext.set(buildSession("admin", 1L, roles, perms));

        assertEquals(roles, SecurityContext.getRoles());
        assertEquals(perms, SecurityContext.getPerms());
    }

    @Test
    @DisplayName("roles / perms 为 null：返回空 Set")
    void testGetRolesPermsNull() {
        SessionContext.set(buildSession("admin", 1L, null, null));

        assertNotNull(SecurityContext.getRoles());
        assertTrue(SecurityContext.getRoles().isEmpty());
        assertNotNull(SecurityContext.getPerms());
        assertTrue(SecurityContext.getPerms().isEmpty());
    }

    // ==================== isAdmin / isMember ====================

    @Test
    @DisplayName("admin 端：isAdmin 返回 true")
    void testIsAdmin() {
        SessionContext.set(buildSession("admin", 1L, null, null));

        assertTrue(SecurityContext.isAdmin());
        assertFalse(SecurityContext.isMember());
    }

    @Test
    @DisplayName("member 端：isMember 返回 true")
    void testIsMember() {
        SessionContext.set(buildSession("member", 1L, null, null));

        assertFalse(SecurityContext.isAdmin());
        assertTrue(SecurityContext.isMember());
    }

    // ==================== requireXxx ====================

    @Test
    @DisplayName("未登录：requireLogin 抛 401")
    void testRequireLoginNotLogin() {
        assertThrows(UnauthorizedException.class, SecurityContext::requireLogin);
        assertThrows(UnauthorizedException.class, SecurityContext::requireUser);
        assertThrows(UnauthorizedException.class, SecurityContext::requireUserId);
        assertThrows(UnauthorizedException.class, SecurityContext::requireUsername);
    }

    @Test
    @DisplayName("登录后：requireLogin 返回 SessionInfo")
    void testRequireLogin() {
        SessionInfo info = buildSession("admin", 1L, null, null);
        SessionContext.set(info);

        assertEquals(info, SecurityContext.requireLogin());
        assertEquals(1L, SecurityContext.requireUserId());
        assertEquals("user1", SecurityContext.requireUsername());
    }

    // ==================== clear ====================

    @Test
    @DisplayName("clear 后所有 get 返回 null / 空")
    void testClear() {
        SessionContext.set(buildSession("admin", 1L,
                Collections.singleton("admin"), null));
        assertTrue(SecurityContext.isLogin());

        SessionContext.clear();

        assertFalse(SecurityContext.isLogin());
        assertNull(SecurityContext.getSession());
        assertNull(SecurityContext.getUserId());
    }

    // ==================== 端类型与 UserType 无关 ====================

    @Test
    @DisplayName("ClientType 与 UserType 解耦，isAdmin 判断的是 clientType")
    void testIsAdminDependsOnClientType() {
        // 直接设置 clientType=admin，不管 UserType 是什么
        SessionContext.set(buildSession(ClientType.ADMIN.getCode(), 1L, null, null));

        assertTrue(SecurityContext.isAdmin());
        assertFalse(SecurityContext.isMember());
    }


}
