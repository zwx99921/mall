package com.we.mall.gateway.util;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.we.mall.common.core.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 响应工具
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
public final class ResponseUtils {

    /**
     * 用 Spring 的构建器，带 JavaTimeModule、Long 转 String
     */
    private static final ObjectMapper MAPPER = Jackson2ObjectMapperBuilder.json().build();

    private ResponseUtils() {
    }

    /**
     * 写出 JSON
     */
    public static Mono<Void> write(ServerWebExchange exchange, HttpStatus status, R<?> body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.fromSupplier(() -> {
            try {
                byte[] bytes = MAPPER.writeValueAsBytes(body);
                return response.bufferFactory().wrap(bytes);
            } catch (Exception e) {
                log.error("响应序列化失败", e);
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                String fallback = JSONUtil.toJsonStr(body);
                return response.bufferFactory().wrap(fallback.getBytes(StandardCharsets.UTF_8));
            }
        }));
    }
}
