package com.we.mall.common.security.interceptor;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.assembler.SessionInfoAssembler;
import com.we.mall.common.security.authenticator.TokenAuthenticator;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器（业务服务用）
 * <p>
 * 支持两种模式：
 * <ul>
 *     <li>{@code gatewayMode=true}：从网关下发的内部 header 还原上下文</li>
 *     <li>{@code gatewayMode=false}：从 Authorization token 解析</li>
 * </ul>
 * <p>
 * 本类只做流程编排，具体装配/认证逻辑抽到：
 * <ul>
 *     <li>{@link SessionInfoAssembler} - 网关模式：header → SessionInfo</li>
 *     <li>{@link TokenAuthenticator}    - Token 模式：token → SessionInfo</li>
 * </ul>
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenAuthenticator tokenAuthenticator;
    private final boolean required;
    private final boolean gatewayMode;

    public AuthInterceptor(TokenAuthenticator tokenAuthenticator,
                           boolean required,
                           boolean gatewayMode) {
        this.tokenAuthenticator = tokenAuthenticator;
        this.required = required;
        this.gatewayMode = gatewayMode;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        SessionInfo sessionInfo = gatewayMode ? SessionInfoAssembler.fromHeaders(request) : tokenAuthenticator.authenticate(request);

        log.debug("AuthInterceptor preHandle sessionInfo: {}", sessionInfo);

        if (sessionInfo == null) {
            if (required) {
                throw UnauthorizedException.notLogin();
            }
            return true;
        }

        SessionContext.set(sessionInfo);
        log.debug("auth ok, mode={}, userId={}, clientType={}", gatewayMode ? "gateway" : "token", sessionInfo.getUser().getUserId(), sessionInfo.getClientType());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 必须清理，防止线程池复用串号
        SessionContext.clear();
    }

}
