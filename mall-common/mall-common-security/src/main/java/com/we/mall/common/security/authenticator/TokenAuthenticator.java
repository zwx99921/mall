package com.we.mall.common.security.authenticator;

import com.we.mall.common.core.constant.JwtConstants;
import com.we.mall.common.core.enums.UserType;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.jwt.util.JwtUtils;
import com.we.mall.common.security.resolver.ClientTypeResolver;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.service.SessionService;

import javax.servlet.http.HttpServletRequest;

/**
 * Token 认证器
 * <p>
 * 从请求 header 的 token 解析出 {@link SessionContext}。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public class TokenAuthenticator {

    private final SessionService sessionService;
    private final JwtServiceProvider jwtServiceProvider;
    private final ClientTypeResolver clientTypeResolver;

    public TokenAuthenticator(SessionService sessionService,
                              JwtServiceProvider jwtServiceProvider,
                              ClientTypeResolver clientTypeResolver) {
        this.sessionService = sessionService;
        this.jwtServiceProvider = jwtServiceProvider;
        this.clientTypeResolver = clientTypeResolver;
    }

    /**
     * 从请求中解析并认证，成功返回 SessionInfo，失败返回 null
     *
     * @param request 请求
     * @return SessionInfo 或 null
     */
    public SessionContext authenticate(HttpServletRequest request) {

        if (sessionService == null || jwtServiceProvider == null || clientTypeResolver == null) {
            return null;
        }

        // 拿 token
        String token = JwtUtils.parseBearer(request.getHeader(JwtConstants.HEADER_AUTH));
        if (token == null) {
            return null;
        }

        // 从token 中解析 UserType，然后推出 ClientType
        UserType userType = JwtUtils.extractUserType(token);
        if (userType == null) {
            return null;
        }

        ClientType clientType = ClientType.fromUserType(userType);
        if (clientType == null) {
            return null;
        }

        // 按端拿 JwtService
        JwtService jwtService = jwtServiceProvider.get(userType);
        if (jwtService == null) {
            return null;
        }

        // 校验 token
        if (!jwtService.validate(token)) {
            return null;
        }

        // 拿 sessionId
        String sessionId = jwtService.getSessionId(token);
        if (sessionId == null) {
            return null;
        }

        // 查 session（会刷新 TTL）
        SessionInfo session = sessionService.getAndRefresh(clientType, sessionId);
        if (session == null) {
            return null;
        }

        // 查 SessionUser
        SessionUser user = sessionService.getSessionUser(clientType, session.getUserId());

        // 组装 SecurityContext
        return SessionContext.builder()
                .session(session)
                .user(user)
                .build();
    }

}
