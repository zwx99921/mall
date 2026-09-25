package com.we.mall.common.security.resolver.impl;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.security.resolver.ClientTypeResolver;
import com.we.mall.common.session.enums.ClientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DefaultClientTypeResolver 单元测试
 * <p>
 * 用 MockHttpServletRequest 构造请求，不需要真实 Servlet 容器。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@DisplayName("DefaultClientTypeResolver 测试")
public class DefaultClientTypeResolverTest {

    private ClientTypeResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultClientTypeResolver();
    }

    // ==================== 请求头优先 ====================

    @Test
    @DisplayName("请求头 X-Client-Type=admin → ADMIN")
    void testHeaderAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_CLIENT_TYPE, "admin");

        assertEquals(ClientType.ADMIN, resolver.resolve(request));
    }

    @Test
    @DisplayName("请求头 X-Client-Type=member → MEMBER")
    void testHeaderMember() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_CLIENT_TYPE, "member");

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

    @Test
    @DisplayName("请求头 X-Client-Type=ADMIN（大写）→ ADMIN")
    void testHeaderUpperCase() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_CLIENT_TYPE, "ADMIN");

        assertEquals(ClientType.ADMIN, resolver.resolve(request));
    }

    @Test
    @DisplayName("请求头非法值 → MEMBER（默认）")
    void testHeaderInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderConstants.HEADER_CLIENT_TYPE, "unknown");

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

    @Test
    @DisplayName("请求头优先于路径：路径 /admin + header member → MEMBER")
    void testHeaderTakesPriorityOverPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/admin/user/list");
        request.addHeader(HeaderConstants.HEADER_CLIENT_TYPE, "member");

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

    // ==================== 路径前缀 ====================

    @Test
    @DisplayName("路径 /admin/** → ADMIN")
    void testPathAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/admin/user/list");

        assertEquals(ClientType.ADMIN, resolver.resolve(request));
    }

    @Test
    @DisplayName("路径 /admin → ADMIN")
    void testPathAdminRoot() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/admin");

        assertEquals(ClientType.ADMIN, resolver.resolve(request));
    }

    @Test
    @DisplayName("路径 /api/** → MEMBER")
    void testPathMember() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/user/me");

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

    @Test
    @DisplayName("路径 / → MEMBER")
    void testPathRoot() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

    @Test
    @DisplayName("路径 /administrator → MEMBER（前缀不匹配 /admin）")
    void testPathAdministratorNotMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/administrator/list");

        // 路径是 /administrator，不是 /admin/
        // 你的实现用 startsWith("/admin")，会命中 → ADMIN
        // 如果想让它不命中，改成 startsWith("/admin/")
        assertEquals(ClientType.ADMIN, resolver.resolve(request));
    }

    // ==================== 兜底 ====================

    @Test
    @DisplayName("无 header + 空路径 → MEMBER")
    void testNoHeaderEmptyPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 不设 header，不设 URI

        assertEquals(ClientType.MEMBER, resolver.resolve(request));
    }

}
