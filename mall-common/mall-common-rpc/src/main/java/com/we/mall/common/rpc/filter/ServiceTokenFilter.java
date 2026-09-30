package com.we.mall.common.rpc.filter;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.ForbiddenException;
import com.we.mall.common.core.util.PathMatcherUtils;
import com.we.mall.common.rpc.properties.RpcProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 服务间调用校验 Filter
 * <p>
 * 对 Controller 和 Actuator 端点都生效。 *
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class ServiceTokenFilter extends OncePerRequestFilter {

    private final RpcProperties rpcProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (PathMatcherUtils.matchesAny(rpcProperties.getInternalPaths(), uri)) {
            String token = request.getHeader(HeaderConstants.HEADER_INTERNAL_SERVICE_TOKEN);
            if (!StringUtils.hasText(token) || !token.equals(rpcProperties.getServiceToken())) {
                log.warn("服务间调用校验失败: uri={}", uri);
                throw ForbiddenException.of(ResultCode.REMOTE_ERROR, "非法内部调用");
            }
        }
        chain.doFilter(request, response);
    }
}
