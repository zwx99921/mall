package com.we.mall.gateway.handler;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.core.exception.ForbiddenException;
import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.core.result.R;
import com.we.mall.gateway.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

/**
 * 网关全局异常处理
 * <p>
 * 实现 ErrorWebExceptionHandler，拦截 Filter、路由、Controller 的所有异常。
 * <p>
 * 处理顺序：
 * 1. 响应已提交 → 不再处理
 * 2. 业务异常 → 200
 * 3. ResponseStatusException → 对应 status
 * 4. 服务找不到 → 503
 * 5. 连接失败 → 502
 * 6. 超时 → 504
 * 7. 兜底 → 500
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@Slf4j
@Order(-2)   // 必须比默认的 DefaultErrorWebExceptionHandler 靠前
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 响应已提交，无法再改
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String path = exchange.getRequest().getPath().value();

        // 未认证 → 401
        if (ex instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedException = (UnauthorizedException) ex;
            log.warn("网关未认证: path={}, code={}, msg={}", path, unauthorizedException.getCode(), unauthorizedException.getMessage());
            return ResponseUtils.write(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    R.fail(unauthorizedException.getResultCode())
            );
        }

        // 无权限 → 403
        if (ex instanceof ForbiddenException) {
            ForbiddenException forbiddenException = (ForbiddenException) ex;
            log.warn("网关无权限: path={}, code={}, msg={}", path, forbiddenException.getCode(), forbiddenException.getMessage());
            return ResponseUtils.write(
                    exchange,
                    HttpStatus.FORBIDDEN,
                    R.fail(forbiddenException.getResultCode())
            );
        }

        // 业务异常（网关自身抛的）
        // 业务异常 → 200
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            log.warn("网关业务异常: path={}, code={}, msg={}", path, businessException.getCode(), businessException.getMessage());

            return ResponseUtils.write(
                    exchange,
                    HttpStatus.OK,
                    R.fail(businessException.getResultCode())
            );
        }

        // 连接失败（下游挂了）
        // 连接失败 → 502
        if (ex instanceof ConnectException) {
            log.error("网关连接失败: path={}, msg={}", path, ex.getMessage());
            return ResponseUtils.write(
                    exchange,
                    HttpStatus.BAD_GATEWAY,
                    R.fail(com.we.mall.common.core.enums.ResultCode.REMOTE_ERROR));
        }

        // 超时
        // 超时 → 504
        if (ex instanceof TimeoutException) {
            log.error("网关请求超时: path={}, msg={}", path, ex.getMessage());

            return ResponseUtils.write(
                    exchange,
                    HttpStatus.GATEWAY_TIMEOUT,
                    R.fail(ResultCode.REMOTE_TIMEOUT)
            );
        }

        // 服务找不到（Nacos 没注册）
        // 服务找不到 → 503
        if (ex instanceof NotFoundException) {
            log.error("网关路由失败: path={}, msg={}", path, ex.getMessage());
            return ResponseUtils.write(
                    exchange,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    R.fail(ResultCode.SERVICE_ERROR)
            );
        }

        // ResponseStatusException（路由找不到、404 等）
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            log.warn("网关响应异常: path={}, status={}, msg={}", path, responseStatusException.getStatus(), responseStatusException.getReason());

            return ResponseUtils.write(
                    exchange,
                    responseStatusException.getStatus(),
                    R.fail(responseStatusException.getStatus().value(), responseStatusException.getReason())
            );
        }

        // 兜底
        // 兜底 → 500
        log.error("网关未知异常: path={}", path, ex);
        return ResponseUtils.write(
                exchange,
                HttpStatus.INTERNAL_SERVER_ERROR,
                R.fail(ResultCode.SERVICE_ERROR)
        );
    }
}
