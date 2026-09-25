package com.we.mall.common.jwt.service;

import com.we.mall.common.core.enums.UserType;
import io.jsonwebtoken.Claims;

import java.util.Set;

/**
 * Jwt 服务接口
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public interface JwtService {

    // ==================== 生成 ====================

    /**
     * 生成 access token
     *
     * @param userId    用户 ID
     * @param username  用户名
     * @param roles     角色
     * @param perms     权限
     * @param sessionId 会话 ID
     * @return access token
     */
    String createAccessToken(Long userId, String username,
                             Set<String> roles, Set<String> perms,
                             String sessionId);

    /**
     * 生成 refresh token
     *
     * @param userId    用户 ID
     * @param username  用户名
     * @param sessionId 会话 ID
     * @return refresh token
     */
    String createRefreshToken(Long userId, String username, String sessionId);

    // ==================== 解析 ====================

    /**
     * 解析 token，失败抛异常
     *
     * @param token token
     * @return claims
     * @throws com.we.mall.common.jwt.exception.TokenException 过期 / 无效
     */
    Claims parse(String token);

    /**
     * 静默解析，失败返回 null
     *
     * @param token token
     * @return claims 或 null
     */
    Claims parseQuietly(String token);

    /**
     * 校验 token 是否有效
     *
     * @param token token
     * @return true 有效
     */
    boolean validate(String token);

    // ==================== Claim 读取 ====================

    /**
     * 获取用户 ID
     */
    Long getUserId(String token);

    /**
     * 获取用户名
     */
    String getUsername(String token);

    /**
     * 获取角色
     */
    Set<String> getRoles(String token);

    /**
     * 获取权限
     */
    Set<String> getPerms(String token);

    /**
     * 获取会话 ID
     */
    String getSessionId(String token);

    /**
     * 获取 Token 类型
     */
    String getTokenType(String token);

    /**
     * 获取用户类型
     */
    UserType getUserType();

    // ==================== 配置读取 ====================

    /**
     * access token 有效期（毫秒）
     */
    Long getExpire();

    /**
     * access token 有效期（秒）
     */
    Long getExpireSeconds();

    /**
     * refresh token 有效期（毫秒）
     */
    Long getRefreshExpire();

    /**
     * refresh token 有效期（秒）
     */
    Long getRefreshExpireSeconds();

}
