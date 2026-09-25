package com.we.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 日志过滤器
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethodValue();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long cost = System.currentTimeMillis() - start;
                    HttpStatus status = exchange.getResponse().getStatusCode();

                    log.info("{} {} {} {}ms", method, path, status == null ? "-" : status.value(), cost);
                });
    }

    /**
     * 最后执行
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
