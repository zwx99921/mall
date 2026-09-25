package com.we.mall.api.admin.client;

import com.we.mall.api.admin.constant.AdminApiConstants;
import com.we.mall.api.admin.factory.UserFeignFallback;
import com.we.mall.common.core.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@FeignClient(
        name = AdminApiConstants.SERVICE_NAME,
        contextId = AdminApiConstants.LOGIN_LOG_CONTEXT_ID,
        path = AdminApiConstants.INNER_LOGIN_LOG_PREFIX,
        fallbackFactory = UserFeignFallback.class
)
public interface LoginLogFeignClient {

    /**
     * 记录登录日志
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param loginType  登录类型
     * @param status     登录状态
     * @param msg        提示消息
     * @param loginIp    登录IP
     * @param userAgent  UA
     * @param deviceType 设备状态
     * @param deviceName 设备名称
     * @return /
     */
    @PostMapping("/record")
    R<Void> record(Long userId, String username, String loginType,
                   Integer status, String msg,
                   String loginIp, String userAgent,
                   String deviceType, String deviceName);

}
