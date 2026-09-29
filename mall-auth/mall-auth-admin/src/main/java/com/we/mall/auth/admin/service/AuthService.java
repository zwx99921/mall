package com.we.mall.auth.admin.service;

import com.we.mall.auth.admin.model.request.LoginRequest;
import com.we.mall.auth.admin.model.request.RefreshTokenRequest;
import com.we.mall.auth.admin.model.response.TokenResponse;

/**
 * 认证服务
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface AuthService {

    /**
     * 登录
     */
    TokenResponse login(LoginRequest request);

    /**
     * 刷新
     */
    TokenResponse refresh(RefreshTokenRequest request);

    /**
     * 登出
     */
    void logout(String authorization);

}
