package com.we.mall.api.admin.client;

import com.we.mall.api.admin.constant.AdminApiConstants;
import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.api.admin.factory.UserFeignFallback;
import com.we.mall.common.core.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 管理员服务 Feign 客户端
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@FeignClient(
        name = AdminApiConstants.SERVICE_NAME,
        contextId = AdminApiConstants.USER_CONTEXT_ID,
        path = AdminApiConstants.INNER_USER_PREFIX,
        fallbackFactory = UserFeignFallback.class
)
public interface UserFeignClient {

    /**
     * 根据用户名加载管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    @GetMapping("/loadByUsername/{username}")
    R<UserDTO> loadByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID加载管理员
     *
     * @param userId 用户ID
     * @return 管理员信息
     */
    @GetMapping("/loadByUserId/{userId}")
    R<UserDTO> loadByUserId(@PathVariable("userId") Long userId);

}
