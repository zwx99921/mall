package com.we.mall.common.security.interceptor;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.assembler.SecurityContextAssembler;
import com.we.mall.common.security.authenticator.TokenAuthenticator;
import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.security.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 * <p>
 * gatewayMode=true：网关已认证，从 X-Internal-* 拿 sessionId / userId / clientType，查 SessionUser，组 SecurityContext。
 * gatewayMode=false：服务自己验 token，查 SessionInfo + SessionUser，组 SecurityContext。
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenAuthenticator tokenAuthenticator;
    private final SecurityContextAssembler securityContextAssembler;
    private final boolean required;
    private final boolean gatewayMode;

    public AuthInterceptor(TokenAuthenticator tokenAuthenticator,
                           SecurityContextAssembler securityContextAssembler,
                           boolean required,
                           boolean gatewayMode) {
        this.tokenAuthenticator = tokenAuthenticator;
        this.securityContextAssembler = securityContextAssembler;
        this.required = required;
        this.gatewayMode = gatewayMode;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SecurityContext ctx = gatewayMode
                ? securityContextAssembler.loadFromGateway(request)
                : tokenAuthenticator.authenticate(request);

        log.debug("AuthInterceptor preHandle ctx: {}", ctx);

        if (ctx == null) {
            if (required) {
                throw UnauthorizedException.notLogin();
            }
            return true;
        }

        SecurityContextHolder.setContext(ctx);

        log.debug("auth ok, mode={}, userId={}, clientType={}", gatewayMode ? "gateway" : "token", ctx.getUserId(), ctx.getClientType());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 必须清理，防止线程池复用串号
        SecurityContextHolder.clearContext();
    }

}
