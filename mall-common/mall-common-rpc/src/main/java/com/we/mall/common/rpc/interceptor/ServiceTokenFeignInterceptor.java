package com.we.mall.common.rpc.interceptor;

import com.we.mall.common.core.constant.HeaderConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;

/**
 * 服务间通信 - 出站拦截器
 * <p>
 * 只带服务身份凭证，不传用户上下文。
 * <p>
 * /inner/** 是系统对系统调用，业务参数由调用方显式传递。
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
public class ServiceTokenFeignInterceptor implements RequestInterceptor {

    private final String serviceToken;

    public ServiceTokenFeignInterceptor(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (StringUtils.hasText(serviceToken)) {
            requestTemplate.header(HeaderConstants.HEADER_INTERNAL_SERVICE_TOKEN, serviceToken);
        }
    }
}
