package com.we.mall.common.security.interceptor;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.assembler.SessionContextAssembler;
import com.we.mall.common.security.authenticator.TokenAuthenticator;
import com.we.mall.common.security.context.SecurityContextHolder;
import com.we.mall.common.session.context.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
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

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SessionContext ctx = gatewayMode
                ? SessionContextAssembler.fromHeaders(request)
                : tokenAuthenticator.authenticate(request);

        log.debug("AuthInterceptor preHandle ctx: {}", ctx);

        if (ctx == null) {
            if (required) {
                throw UnauthorizedException.notLogin();
            }
            return true;
        }

        SecurityContextHolder.setContext(ctx);

        log.debug("auth ok, mode={}, userId={}, clientType={}", gatewayMode ? "gateway" : "token",
                ctx.getSession().getUserId(), ctx.getSession().getClientType());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 必须清理，防止线程池复用串号
        SecurityContextHolder.clearContext();
    }

}
