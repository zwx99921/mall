package com.we.mall.common.security.resolver;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.enums.ClientType;

import javax.servlet.http.HttpServletRequest;

/**
 * 端类型解析器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public class ClientTypeResolver {

    /**
     * 从请求中解析端类型
     */
    public ClientType resolve(HttpServletRequest request) {
        String header = request.getHeader(HeaderConstants.HEADER_CLIENT_TYPE);

        if (StrUtil.isNotBlank(header)) {
            return ClientType.of(header);
        }
        return null;
    }

}
