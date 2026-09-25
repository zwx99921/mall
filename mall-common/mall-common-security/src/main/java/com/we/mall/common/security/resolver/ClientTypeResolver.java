package com.we.mall.common.security.resolver;

import com.we.mall.common.session.enums.ClientType;

import javax.servlet.http.HttpServletRequest;

/**
 * 端类型解析器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface ClientTypeResolver {

    /**
     * 从请求中解析端类型
     */
    ClientType resolve(HttpServletRequest request);

}
