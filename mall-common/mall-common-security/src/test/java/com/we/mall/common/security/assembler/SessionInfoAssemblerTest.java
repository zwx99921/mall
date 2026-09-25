package com.we.mall.common.security.assembler;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.model.SessionInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SessionInfoAssembler 单元测试
 * <p>
 * 纯静态方法，用 MockHttpServletRequest 构造请求。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@DisplayName("SessionInfoAssembler 测试")
public class SessionInfoAssemblerTest {

    // ==================== 关键 header 缺失 ====================

    @Test
    @DisplayName("无任何 header → 返回 null")
    void testNoHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(SessionInfoAssembler.fromHeaders(request));
    }

    @Test
    @DisplayName("缺 User-Id header → 返回 null")
    void testMissingUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USERNAME, "user1");

        assertNull(SessionInfoAssembler.fromHeaders(request));
    }

    @Test
    @DisplayName("User-Id 为空字符串 → 返回 null")
    void testBlankUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "  ");

        assertNull(SessionInfoAssembler.fromHeaders(request));
    }

    // ==================== 完整装配 ====================

    @Test
    @DisplayName("完整 header → SessionInfo 正确还原")
    void testFullAssemble() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USERNAME, "user1");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_NICKNAME, "张三");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_TENANT_ID, "100");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_SESSION_ID, "sid-abc");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE, "member");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE, "APP");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_ROLES, "user,ops");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_PERMS, "user:read,user:write");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertNotNull(info);
        assertEquals("sid-abc", info.getSessionId());
        assertEquals("member", info.getClientType());
        assertEquals("APP", info.getDeviceType());

        assertNotNull(info.getUser());
        assertEquals(1001L, info.getUser().getUserId());
        assertEquals("user1", info.getUser().getUsername());
        assertEquals("张三", info.getUser().getNickname());
        assertEquals(100L, info.getUser().getTenantId());

        assertEquals(2, info.getRoles().size());
        assertTrue(info.getRoles().contains("user"));
        assertTrue(info.getRoles().contains("ops"));

        assertEquals(2, info.getPerms().size());
        assertTrue(info.getPerms().contains("user:read"));
        assertTrue(info.getPerms().contains("user:write"));
    }

    // ==================== 可选 header ====================

    @Test
    @DisplayName("只配 User-Id，其余可选 header 缺失 → 不抛异常")
    void testOnlyUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertNotNull(info);
        assertEquals(1001L, info.getUser().getUserId());
        assertNull(info.getUser().getUsername());
        assertNull(info.getUser().getNickname());
        assertNull(info.getUser().getTenantId());
        assertNull(info.getSessionId());
        assertNull(info.getClientType());
        assertNull(info.getDeviceType());
        assertNotNull(info.getRoles());
        assertTrue(info.getRoles().isEmpty());
        assertNotNull(info.getPerms());
        assertTrue(info.getPerms().isEmpty());
    }

    @Test
    @DisplayName("Tenant-Id 缺失 → user.tenantId 为 null")
    void testMissingTenantId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertNotNull(info.getUser());
        assertNull(info.getUser().getTenantId());
    }

    // ==================== Roles / Perms 解析 ====================

    @Test
    @DisplayName("Roles 单个值")
    void testSingleRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_ROLES, "admin");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertEquals(1, info.getRoles().size());
        assertTrue(info.getRoles().contains("admin"));
    }

    @Test
    @DisplayName("Roles 多个值带空格 → 自动 trim")
    void testRolesWithSpaces() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_ROLES, " admin , ops , user ");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertEquals(3, info.getRoles().size());
        assertTrue(info.getRoles().contains("admin"));
        assertTrue(info.getRoles().contains("ops"));
        assertTrue(info.getRoles().contains("user"));
    }

    @Test
    @DisplayName("Roles 含空段 → 忽略空段")
    void testRolesWithEmptySegments() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_ROLES, "admin,,ops,");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertEquals(2, info.getRoles().size());
        assertTrue(info.getRoles().contains("admin"));
        assertTrue(info.getRoles().contains("ops"));
    }

    @Test
    @DisplayName("Roles 为空白 → 空集合")
    void testRolesBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_ROLES, "   ");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertNotNull(info.getRoles());
        assertTrue(info.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Perms 多个值 → 正确解析")
    void testMultiplePerms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_PERMS, "user:read,user:write,user:delete");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertEquals(3, info.getPerms().size());
        assertTrue(info.getPerms().contains("user:read"));
        assertTrue(info.getPerms().contains("user:write"));
        assertTrue(info.getPerms().contains("user:delete"));
    }

    // ==================== 端类型 ====================

    @Test
    @DisplayName("clientType=admin → info.clientType = admin")
    void testClientTypeAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");
        request.addHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE, "admin");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertEquals("admin", info.getClientType());
    }

    @Test
    @DisplayName("clientType 缺失 → info.clientType 为 null")
    void testClientTypeMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_INTERNAL_USER_ID, "1001");

        SessionInfo info = SessionInfoAssembler.fromHeaders(request);

        assertNull(info.getClientType());
    }

}
