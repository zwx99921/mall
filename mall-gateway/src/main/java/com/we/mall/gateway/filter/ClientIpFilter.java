//package com.we.mall.gateway.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.net.InetSocketAddress;
//
/// **
// * 客户端 IP 传递过滤器
// * <p>
// * 网关是"代理"，业务服务看到的 RemoteAddr 是网关 IP。
// * 本过滤器把"真实客户端 IP"写进 header，传给下游。
// * <p>
// * 执行顺序在 AuthFilter 之前（所有请求都过）。
// *
// * @author we
// * @date 2026-09-29
// * @description
// */
//@Slf4j
//@Component
//public class ClientIpFilter implements GlobalFilter, Ordered {
//
//    private static final String HEADER_REAL_IP = "X-Real-IP";
//    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        String clientIp = resolveClientIp(request);
//
//        System.out.println("clientIp:" + clientIp);
//
//        ServerHttpRequest mutated = request.mutate()
//                .header(HEADER_REAL_IP, clientIp)
//                .header(HEADER_X_FORWARDED_FOR, buildXff(request, clientIp))
//                .build();
//
//        return chain.filter(exchange.mutate().request(mutated).build());
//    }
//
//    /**
//     * 解析客户端 IP
//     * <p>
//     * 上游可能有 Nginx，优先从 X-Real-IP / X-Forwarded-For 拿。
//     */
//    private String resolveClientIp(ServerHttpRequest request) {
//        // 1. X-Real-IP（Nginx 加的，可信）
//        String realIp = request.getHeaders().getFirst(HEADER_REAL_IP);
//        if (realIp != null && !realIp.isEmpty()) {
//            return realIp;
//        }
//
//        // 2. X-Forwarded-For（取第一个）
//        String xff = request.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
//        if (xff != null && !xff.isEmpty()) {
//            int idx = xff.indexOf(',');
//            return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
//        }
//
//        // 3. RemoteAddr
//        InetSocketAddress remote = request.getRemoteAddress();
//        if (remote != null && remote.getAddress() != null) {
//            return remote.getAddress().getHostAddress();
//        }
//
//        return "unknown";
//    }
//
//    /**
//     * 构造 X-Forwarded-For：客户端 + 已有链
//     */
//    private String buildXff(ServerHttpRequest request, String clientIp) {
//        String existing = request.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
//        if (existing == null || existing.isEmpty()) {
//            return clientIp;
//        }
//        return existing + ", " + clientIp;
//    }
//
//    @Override
//    public int getOrder() {
//        return -200;
//    }
//}
