package com.we.mall.auth.admin.resolver;

import com.we.mall.auth.admin.util.UserAgentUtils;
import com.we.mall.common.web.util.ServletUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 设备名称解析器
 *
 * @author we
 * @date 2026-09-29
 * @description
 */
@Component
public class DeviceNameResolver {

    public String resolve() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return null;
        }
        String ua = request.getHeader(HttpHeaders.USER_AGENT);
        return resolve(ua);
    }

    public String resolve(String userAgent) {
        return UserAgentUtils.parseDeviceName(userAgent);
    }

}
