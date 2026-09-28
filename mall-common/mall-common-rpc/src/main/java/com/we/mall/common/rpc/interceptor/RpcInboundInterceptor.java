package com.we.mall.common.rpc.interceptor;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.ForbiddenException;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 服务间通信 - 入站拦截器
 * <p>
 * 只校验服务身份，不涉及用户上下文。
 * <p>
 * 适用路径：由 {@code mall.rpc.internal-paths} 配置，默认 /inner/**
 * <p>
 * 业务参数由调用方显式传递（路径参数 / 请求参数），
 * 不从 SessionContext 取。
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public class RpcInboundInterceptor implements HandlerInterceptor {

    private final String serviceToken;

    public RpcInboundInterceptor(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(HeaderConstants.HEADER_INTERNAL_SERVICE_TOKEN);
        if (!StringUtils.hasText(token) || !token.equals(serviceToken)) {
            throw ForbiddenException.of(ResultCode.REMOTE_ERROR, "非法内部调用");
        }
        return true;
    }

}
