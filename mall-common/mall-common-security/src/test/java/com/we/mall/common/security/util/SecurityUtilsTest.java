package com.we.mall.common.security.util;

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
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityUtils 单元测试
 * <p>
 * 纯静态工具，不依赖 Spring。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@DisplayName("SecurityUtils 测试")
public class SecurityUtilsTest {

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
    void testIsLoginFalse() {
        assertFalse(SecurityUtils.isLogin());
    }

    @Test
    @DisplayName("登录后：isLogin 返回 true")
    void testIsLoginTrue() {
        SessionContext.set(buildSession("admin", 1L, null, null));
        assertTrue(SecurityUtils.isLogin());
    }

    // ==================== getXxx ====================

    @Test
    @DisplayName("未登录：getXxx 返回 null / 空")
    void testGettersWhenNotLogin() {
        assertNull(SecurityUtils.getSession());
        assertNull(SecurityUtils.getSessionId());
        assertNull(SecurityUtils.getUser());
        assertNull(SecurityUtils.getUserId());
        assertNull(SecurityUtils.getUsername());
        assertNull(SecurityUtils.getNickname());
        assertTrue(SecurityUtils.getRoles().isEmpty());
        assertTrue(SecurityUtils.getPerms().isEmpty());
    }

    @Test
    @DisplayName("登录后：getXxx 返回正确")
    void testGettersWhenLogin() {
        Set<String> roles = new LinkedHashSet<>(Arrays.asList("admin", "ops"));
        Set<String> perms = new LinkedHashSet<>(Arrays.asList("user:read", "user:write"));

        SessionInfo info = buildSession("admin", 1L, roles, perms);
        SessionContext.set(info);

        assertEquals(info, SecurityUtils.getSession());
        assertEquals("sid-1", SecurityUtils.getSessionId());
        assertEquals(1L, SecurityUtils.getUserId());
        assertEquals("user1", SecurityUtils.getUsername());
        assertEquals("昵称1", SecurityUtils.getNickname());
        assertEquals(roles, SecurityUtils.getRoles());
        assertEquals(perms, SecurityUtils.getPerms());
    }

    // ==================== requireXxx ====================

    @Test
    @DisplayName("未登录：requireXxx 抛 401")
    void testRequireWhenNotLogin() {
        assertThrows(UnauthorizedException.class, SecurityUtils::requireUser);
        assertThrows(UnauthorizedException.class, SecurityUtils::requireUserId);
    }

    @Test
    @DisplayName("登录后：requireXxx 返回正确")
    void testRequireWhenLogin() {
        SessionContext.set(buildSession("admin", 1L, null, null));

        assertNotNull(SecurityUtils.requireUser());
        assertEquals(1L, SecurityUtils.requireUserId());
    }

    // ==================== isAdmin / isMember ====================

    @Test
    @DisplayName("admin 端：isAdmin 返回 true")
    void testIsAdmin() {
        SessionContext.set(buildSession(ClientType.ADMIN.getCode(), 1L, null, null));

        assertTrue(SecurityUtils.isAdmin());
        assertFalse(SecurityUtils.isMember());
    }

    @Test
    @DisplayName("member 端：isMember 返回 true")
    void testIsMember() {
        SessionContext.set(buildSession(ClientType.MEMBER.getCode(), 1L, null, null));

        assertFalse(SecurityUtils.isAdmin());
        assertTrue(SecurityUtils.isMember());
    }

    @Test
    @DisplayName("未登录：isAdmin / isMember 都返回 false")
    void testAdminMemberWhenNotLogin() {
        assertFalse(SecurityUtils.isAdmin());
        assertFalse(SecurityUtils.isMember());
    }

    // ==================== 边界 ====================

    @Test
    @DisplayName("SessionInfo.user 为 null：getUserId 返回 null，requireUserId 抛 401")
    void testNullUser() {
        SessionInfo info = new SessionInfo();
        info.setSessionId("sid");
        info.setClientType("admin");
        // user 为 null

        SessionContext.set(info);

        assertNull(SecurityUtils.getUserId());
        assertNull(SecurityUtils.getUsername());
        assertThrows(UnauthorizedException.class, SecurityUtils::requireUserId);
    }

    @Test
    @DisplayName("roles / perms 为 null：getXxx 返回空 Set")
    void testNullRolesPerms() {
        SessionContext.set(buildSession("admin", 1L, null, null));

        assertNotNull(SecurityUtils.getRoles());
        assertTrue(SecurityUtils.getRoles().isEmpty());
        assertNotNull(SecurityUtils.getPerms());
        assertTrue(SecurityUtils.getPerms().isEmpty());
    }


}
