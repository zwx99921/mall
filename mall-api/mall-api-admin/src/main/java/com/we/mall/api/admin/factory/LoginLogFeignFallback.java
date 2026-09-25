package com.we.mall.api.admin.factory;

import com.we.mall.api.admin.client.LoginLogFeignClient;
import com.we.mall.api.admin.constant.AdminApiConstants;
import com.we.mall.common.core.exception.RemoteException;
import com.we.mall.common.core.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 登录日志服务降级
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
public class LoginLogFeignFallback implements FallbackFactory<LoginLogFeignClient> {
    @Override
    public LoginLogFeignClient create(Throwable cause) {
        log.error("登录日志服务调用失败: service={}, cause={}", AdminApiConstants.SERVICE_NAME, cause.getMessage(), cause);
        return new LoginLogFeignClient() {

            @Override
            public R<Void> record(Long userId, String username, String loginType,
                                  Integer status, String msg,
                                  String loginIp, String userAgent,
                                  String deviceType, String deviceName) {
                throw RemoteException.of(AdminApiConstants.SERVICE_NAME, "记录登录日志失败: " + username);
            }
        };
    }
}
