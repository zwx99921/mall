package com.we.mall.auth.admin.controller;

import com.we.mall.auth.admin.annotation.LoginLog;
import com.we.mall.auth.admin.enums.LoginType;
import com.we.mall.auth.admin.model.request.LoginRequest;
import com.we.mall.auth.admin.model.request.RefreshTokenRequest;
import com.we.mall.auth.admin.model.response.TokenResponse;
import com.we.mall.auth.admin.service.AuthService;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.core.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Tag(name = "管理员认证", description = "管理员认证接口")
@Slf4j
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @Operation(summary = "登录", description = "管理员登录")
    @PostMapping("/login")
    @LoginLog(type = LoginType.PASSWORD)
    public R<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return R.ok(authService.login(request));
    }

    /**
     * 刷新
     */
    @Operation(summary = "刷新", description = "管理员刷新Token")
    @PostMapping("/refresh")
    public R<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return R.ok(authService.refresh(request));
    }

    /**
     * 登出
     */
    @Operation(summary = "登出", description = "管理员退出登录")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(HeaderConstants.HEADER_AUTHORIZATION) String authorization) {
        authService.logout(authorization);
        return R.ok();
    }

}
