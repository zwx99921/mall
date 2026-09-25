package com.we.mall.api.admin.factory;

import com.we.mall.api.admin.client.UserFeignClient;
import com.we.mall.api.admin.constant.AdminApiConstants;
import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.common.core.exception.RemoteException;
import com.we.mall.common.core.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 用户服务降级处理
 *
 * @author we
 * @date 2026-09-18
 * @description
 */

@Slf4j
public class UserFeignFallback implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("用户服务调用失败: service={}, cause={}", AdminApiConstants.SERVICE_NAME, cause.getMessage(), cause);

        return new UserFeignClient() {
            @Override
            public R<UserDTO> loadByUsername(String username) {
                throw RemoteException.of(AdminApiConstants.SERVICE_NAME, "获取用户失败: " + username);
            }

            @Override
            public R<UserDTO> loadByUserId(Long userId) {
                throw RemoteException.of(AdminApiConstants.SERVICE_NAME, "获取用户失败: " + userId);
            }
        };
    }
}
