package com.we.mall.modules.admin.controller.internal;

import com.we.mall.api.admin.client.UserFeignClient;
import com.we.mall.api.admin.constant.AdminApiConstants;
import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.common.core.result.R;
import com.we.mall.modules.admin.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户内部接口
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Hidden
@RestController
@RequestMapping(AdminApiConstants.INNER_USER_PREFIX)
@RequiredArgsConstructor
public class InnerUserController implements UserFeignClient {

    private final UserService userService;

    @Override
    @GetMapping("/loadByUsername/{username}")
    public R<UserDTO> loadByUsername(@PathVariable("username") String username) {
        return R.ok(userService.loadByUsername(username));
    }

    @Override
    @GetMapping("/loadByUserId/{userId}")
    public R<UserDTO> loadByUserId(@PathVariable("userId") Long userId) {
        return R.ok(userService.loadByUserId(userId));
    }
}
