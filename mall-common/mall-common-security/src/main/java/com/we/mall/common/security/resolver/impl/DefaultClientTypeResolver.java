package com.we.mall.common.security.resolver.impl;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.security.resolver.ClientTypeResolver;
import com.we.mall.common.session.enums.ClientType;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认端类型解析器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public class DefaultClientTypeResolver implements ClientTypeResolver {

    @Override
    public ClientType resolve(HttpServletRequest request) {
        // 1. 优先看请求头
        String header = request.getHeader(HeaderConstants.HEADER_CLIENT_TYPE);

        if (StrUtil.isNotBlank(header)) {
            return ClientType.of(header);
        }
        return null;
    }
}
