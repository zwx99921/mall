package com.we.mall.common.security.authenticator;

import com.we.mall.common.core.constant.JwtConstants;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.security.resolver.ClientTypeResolver;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TokenAuthenticator 单元测试
 * <p>
 * 覆盖 6 步链路的成功与失败分支。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@DisplayName("TokenAuthenticator 测试")
public class TokenAuthenticatorTest {

    private static final String TOKEN = "test-token";
    private static final String SESSION_ID = "sid-abc";
    private SessionService sessionService;
    private JwtServiceProvider jwtServiceProvider;
    private ClientTypeResolver clientTypeResolver;
    private JwtService jwtService;
    private TokenAuthenticator authenticator;

    @BeforeEach
    void setUp() {
        sessionService = mock(SessionService.class);
        jwtServiceProvider = mock(JwtServiceProvider.class);
        clientTypeResolver = mock(ClientTypeResolver.class);
        jwtService = mock(JwtService.class);

        authenticator = new TokenAuthenticator(
                sessionService, jwtServiceProvider, clientTypeResolver);
    }

    // ==================== 依赖缺失 ====================

    @Test
    @DisplayName("依赖为 null → 直接返回 null")
    void testNullDependencies() {
        TokenAuthenticator nullAuth = new TokenAuthenticator(null, null, null);
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(nullAuth.authenticate(request));
    }

    // ==================== 1. 拿 token ====================

    @Test
    @DisplayName("无 Authorization header → 返回 null")
    void testNoAuthHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(authenticator.authenticate(request));
    }

    @Test
    @DisplayName("Authorization 非 Bearer 格式 → 返回 null")
    void testNonBearerHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER_AUTH, "Basic abc");

        assertNull(authenticator.authenticate(request));
    }

    @Test
    @DisplayName("Bearer 后为空 → 返回 null")
    void testBlankBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER_AUTH, "Bearer ");

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 2. 端类型解析 ====================

    @Test
    @DisplayName("clientTypeResolver 返回 null → 返回 null")
    void testClientTypeNull() {
        MockHttpServletRequest request = buildRequestWithToken();

        when(clientTypeResolver.resolve(request)).thenReturn(null);

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 3. JwtService 获取 ====================

    @Test
    @DisplayName("jwtServiceProvider 找不到 JwtService → 返回 null")
    void testJwtServiceNotFound() {
        MockHttpServletRequest request = buildRequestWithToken();

        when(clientTypeResolver.resolve(request)).thenReturn(ClientType.ADMIN);
        when(jwtServiceProvider.get(any())).thenReturn(null);

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 4. token 校验 ====================

    @Test
    @DisplayName("token 校验失败 → 返回 null")
    void testTokenInvalid() {
        MockHttpServletRequest request = buildRequestWithToken();

        when(clientTypeResolver.resolve(request)).thenReturn(ClientType.ADMIN);
        when(jwtServiceProvider.get(any())).thenReturn(jwtService);
        when(jwtService.validate(TOKEN)).thenReturn(false);

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 5. sessionId ====================

    @Test
    @DisplayName("token 中 sessionId 为 null → 返回 null")
    void testSessionIdNull() {
        MockHttpServletRequest request = buildRequestWithToken();

        when(clientTypeResolver.resolve(request)).thenReturn(ClientType.ADMIN);
        when(jwtServiceProvider.get(any())).thenReturn(jwtService);
        when(jwtService.validate(TOKEN)).thenReturn(true);
        when(jwtService.getSessionId(TOKEN)).thenReturn(null);

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 6. session 查询 ====================

    @Test
    @DisplayName("sessionService 返回 null → 返回 null")
    void testSessionNotFound() {
        MockHttpServletRequest request = buildRequestWithToken();

        when(clientTypeResolver.resolve(request)).thenReturn(ClientType.ADMIN);
        when(jwtServiceProvider.get(any())).thenReturn(jwtService);
        when(jwtService.validate(TOKEN)).thenReturn(true);
        when(jwtService.getSessionId(TOKEN)).thenReturn(SESSION_ID);
        when(sessionService.getAndRefresh(any(), eq(SESSION_ID))).thenReturn(null);

        assertNull(authenticator.authenticate(request));
    }

    // ==================== 成功 ====================

    @Test
    @DisplayName("完整链路成功 → 返回 SessionInfo")
    void testSuccess() {
        MockHttpServletRequest request = buildRequestWithToken();

        SessionInfo expected = new SessionInfo();
        expected.setSessionId(SESSION_ID);

        when(clientTypeResolver.resolve(request)).thenReturn(ClientType.ADMIN);
        when(jwtServiceProvider.get(any())).thenReturn(jwtService);
        when(jwtService.validate(TOKEN)).thenReturn(true);
        when(jwtService.getSessionId(TOKEN)).thenReturn(SESSION_ID);
        when(sessionService.getAndRefresh(any(), eq(SESSION_ID))).thenReturn(expected);

        SessionInfo actual = authenticator.authenticate(request);

        assertNotNull(actual);
        assertEquals(SESSION_ID, actual.getSessionId());

        // 验证调用链
        verify(clientTypeResolver).resolve(request);
        verify(jwtServiceProvider).get(any());
        verify(jwtService).validate(TOKEN);
        verify(jwtService).getSessionId(TOKEN);
        verify(sessionService).getAndRefresh(any(), eq(SESSION_ID));
    }

    // ==================== 工具方法 ====================

    private MockHttpServletRequest buildRequestWithToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER_AUTH, "Bearer " + TOKEN);
        return request;
    }

}
