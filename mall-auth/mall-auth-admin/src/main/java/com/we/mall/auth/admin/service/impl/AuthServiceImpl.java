package com.we.mall.auth.admin.service.impl;

import com.we.mall.api.admin.client.UserFeignClient;
import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.auth.admin.model.request.LoginRequest;
import com.we.mall.auth.admin.model.request.RefreshTokenRequest;
import com.we.mall.auth.admin.model.response.TokenResponse;
import com.we.mall.auth.admin.resolver.DeviceNameResolver;
import com.we.mall.auth.admin.resolver.DeviceTypeResolver;
import com.we.mall.auth.admin.service.AuthService;
import com.we.mall.auth.admin.service.CaptchaService;
import com.we.mall.auth.admin.service.PasswordService;
import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.core.enums.DeviceType;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.jwt.util.JwtUtils;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.common.web.util.IpUtils;
import com.we.mall.common.web.util.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 认证服务实现
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final ClientType CLIENT = ClientType.ADMIN;

    private final JwtServiceProvider jwtServiceProvider;
    private final CaptchaService captchaService;
    private final PasswordService passwordService;
    private final SessionService sessionService;
    private final UserFeignClient userFeignClient;
    private final DeviceTypeResolver deviceTypeResolver;
    private final DeviceNameResolver deviceNameResolver;

    @Override
    public TokenResponse login(LoginRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String captcha = request.getCaptcha();
        String captchaId = request.getCaptchaId();

        // 校验验证码
        captchaService.verify(captchaId, captcha);

        // Feign 拿用户
        UserDTO user = userFeignClient.loadByUsername(username).getData();
        if (user == null) {
            throw BusinessException.of(ResultCode.LOGIN_FAILED);
        }

        // 状态校验
        if (user.getStatus() == null || user.getStatus().isDisabled()) {
            log.warn("登录失败-账号已禁用: username={}", user.getUsername());
            throw BusinessException.of(ResultCode.ACCOUNT_DISABLED);
        }

        // 验证
        passwordService.verify(username, user.getPassword(), password);

        String ip = IpUtils.getIpAddr();
        String userAgent = ServletUtils.getHeader(HttpHeaders.USER_AGENT);

        DeviceType deviceType = deviceTypeResolver.resolve(userAgent);
        String deviceName = deviceNameResolver.resolve(userAgent);

        Set<String> roles = user.getRoles();
        Set<String> perms = user.getPerms();

        // 构造 SessionUser
        SessionUser sessionUser = SessionUser.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(roles)
                .perms(perms)
                .build();

        // 生成 SessionId
        String sessionId = sessionService.create(CLIENT, sessionUser, ip, userAgent, deviceType, deviceName);

        // 创建Token
        JwtService jwtService = jwtServiceProvider.get(CLIENT);
        String accessToken = jwtService.createAccessToken(user.getUserId(), user.getUsername(), roles, perms, sessionId);
        String refreshToken = jwtService.createRefreshToken(user.getUserId(), user.getUsername(), sessionId);

        // 成功
        log.info("认证成功: userId={}, username={},sessionId={}", user.getUserId(), user.getUsername(), sessionId);

        return TokenResponse.of(accessToken, refreshToken, jwtService.getExpire());
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        JwtService jwtService = jwtServiceProvider.get(CLIENT);

        // 校验 refreshToken
        if (!jwtService.validate(refreshToken)) {
            throw UnauthorizedException.of(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 拿 sessionId
        String sessionId = jwtService.getSessionId(refreshToken);
        if (sessionId == null) {
            throw UnauthorizedException.of(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 查 session（必须还在）
        SessionInfo info = sessionService.getAndRefresh(CLIENT, sessionId);
        if (info == null) {
            throw UnauthorizedException.of(ResultCode.SESSION_EXPIRED);
        }

        SessionUser user = sessionService.getSessionUser(CLIENT, info.getUserId());
        if (user == null) {
            throw UnauthorizedException.of(ResultCode.SESSION_EXPIRED);
        }

        // 重新签发 accessToken
        Long userId = info.getUserId();
        String username = user.getUsername();
        String newAccessToken = jwtService.createAccessToken(userId, username, user.getRoles(), user.getPerms(), sessionId);

        log.info("刷新 token 成功: userId={}, sessionId={}", userId, sessionId);

        return TokenResponse.of(newAccessToken, refreshToken, jwtService.getExpireSeconds());
    }

    @Override
    public void logout(String authorization) {
        String token = JwtUtils.parseBearer(authorization);
        if (token == null) {
            log.warn("登出失败: token 为空");
            return;
        }
        JwtService jwtService = jwtServiceProvider.get(CLIENT);
        if (!jwtService.validate(token)) {
            log.warn("登出失败: token 无效");
            return;
        }

        String sessionId = jwtService.getSessionId(token);
        if (sessionId == null) {
            log.warn("登出失败: sessionId 为空");
            return;
        }
        sessionService.destroy(CLIENT, sessionId);

        log.info("登出成功: sessionId={}", sessionId);
    }

    @Override
    public void kickUser(Long userId) {
        if (userId == null) {
            return;
        }
        sessionService.kickAll(CLIENT, userId);
        log.info("强制下线: userId={}", userId);
    }
}
