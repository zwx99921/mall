package com.we.mall.gateway.auth;

import com.we.mall.common.core.constant.JwtConstants;
import com.we.mall.common.core.enums.UserType;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.jwt.util.JwtUtils;
import com.we.mall.common.session.enums.ClientType;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 网关认证服务
 * <p>
 * 认证链：token → clientType → jwtService → session
 * <p>
 * 任一环节失败返回 null。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthenticate {

    private final JwtServiceProvider jwtServiceProvider;
    private final SessionService sessionService;

    /**
     * 从请求中认证，成功返回 SessionInfo，失败返回 null
     */
    public SessionInfo authenticate(ServerHttpRequest request) {
        String path = request.getPath().value();

        // 拿 token
        String token = JwtUtils.parseBearer(request.getHeaders().getFirst(JwtConstants.HEADER_AUTH));
        if (!StringUtils.hasText(token)) {
            log.debug("auth fail: no token, path={}", path);
            return null;
        }

        UserType userType = JwtUtils.extractUserType(token);
        if (userType == null) {
            log.debug("auth fail: unknown user type, path={}", path);
            return null;
        }

        // 解析端类型
        ClientType clientType = ClientType.fromUserType(userType);
        if (clientType == null) {
            log.debug("auth fail: unknown client type, path={}", path);
            return null;
        }

        // 校验 token
        JwtService jwtService = jwtServiceProvider.get(userType);
        if (jwtService == null || !jwtService.validate(token)) {
            log.debug("auth fail: invalid token, path={}", path);
            return null;
        }

        // 拿 sessionId
        String sessionId = jwtService.getSessionId(token);
        if (!StringUtils.hasText(sessionId)) {
            log.debug("auth fail: no sessionId, path={}", path);
            return null;
        }

        // 查 session
        SessionInfo info = sessionService.getAndRefresh(clientType, sessionId);
        if (info == null || info.getUser() == null) {
            log.debug("auth fail: session not found, path={}", path);
            return null;
        }

        return info;
    }

}
