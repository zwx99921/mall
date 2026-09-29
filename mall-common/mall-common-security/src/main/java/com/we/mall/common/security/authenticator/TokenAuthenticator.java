package com.we.mall.common.security.authenticator;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.jwt.util.JwtUtils;
import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.service.SessionService;

import javax.servlet.http.HttpServletRequest;

/**
 * Token 认证器
 * <p>
 * 从请求 header 的 token 解析出 {@link SecurityContext}。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public class TokenAuthenticator {

    private final SessionService sessionService;
    private final JwtServiceProvider jwtServiceProvider;

    public TokenAuthenticator(SessionService sessionService,
                              JwtServiceProvider jwtServiceProvider) {
        this.sessionService = sessionService;
        this.jwtServiceProvider = jwtServiceProvider;
    }

    /**
     * 从请求中解析并认证，成功返回 SessionInfo，失败返回 null
     *
     * @param request 请求
     * @return SessionInfo 或 null
     */
    public SecurityContext authenticate(HttpServletRequest request) {

        if (sessionService == null || jwtServiceProvider == null) {
            return null;
        }

        // Token
        String token = JwtUtils.parseBearer(request.getHeader(HeaderConstants.HEADER_AUTHORIZATION));
        if (token == null) {
            return null;
        }

        // ClientType
        ClientType clientType = JwtUtils.extractClientType(token);
        if (clientType == null) {
            return null;
        }
        // JwtService
        JwtService jwtService = jwtServiceProvider.get(clientType);
        if (jwtService == null) {
            return null;
        }

        // 校验 Token
        if (!jwtService.validate(token)) {
            return null;
        }

        // SessionId
        String sessionId = jwtService.getSessionId(token);
        if (sessionId == null) {
            return null;
        }

        // SessionInfo（会刷新 TTL）
        SessionInfo session = sessionService.getAndRefresh(clientType, sessionId);
        if (session == null) {
            return null;
        }

        // SessionUser
        SessionUser user = sessionService.getSessionUser(clientType, session.getUserId());

        // SecurityContext（扁平）
        return SecurityContext.builder()
                .sessionId(session.getSessionId())
                .clientType(session.getClientType())
                .deviceType(session.getDeviceType())
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(user.getRoles())
                .perms(user.getPerms())
                .build();
    }

}
