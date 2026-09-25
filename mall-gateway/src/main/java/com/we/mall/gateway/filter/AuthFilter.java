package com.we.mall.gateway.filter;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.gateway.auth.GatewayAuthenticate;
import com.we.mall.gateway.properties.GatewayProperties;
import com.we.mall.gateway.util.InternalHeaderUtils;
import com.we.mall.gateway.util.WhiteListMatcherUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证过滤器
 * <p>
 * 只做流程编排：白名单 → 认证 → 下发 header → 放行 / 401。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;
    private final GatewayAuthenticate gatewayAuthService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getPath().value();

        // OPTIONS 预检直接放行
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // 白名单放行
        if (WhiteListMatcherUtils.match(gatewayProperties.getAllWhiteList(), path)) {
            return chain.filter(exchange);
        }

        // 认证
        SessionInfo session = gatewayAuthService.authenticate(request);
        if (session == null) {
            throw new UnauthorizedException(ResultCode.UNAUTHORIZED);
        }

        // 下发 header，放行
        ServerHttpRequest mutated = InternalHeaderUtils.buildRequest(exchange.getRequest(), session);
        log.info(">>> mutated headers = {}", mutated.getHeaders());


        log.debug("gateway auth ok: userId={}, clientType={}, path={}", session.getUser().getUserId(), session.getClientType(), path);

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    /**
     * 在跨域过滤器（-200）之后，在路由转发之前执行
     */
    @Override
    public int getOrder() {
        return -100; // 靠前执行
    }
}
