package com.we.mall.common.core.constant;

/**
 * JWT 常量
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public final class JwtConstants {

    public static final String JWT = "JWT";
    /**
     * Token 前缀（无空格，小写）—— 用于 OpenAPI
     */
    public static final String TOKEN_SCHEME = "bearer";
    /**
     * 用户类型：ADMIN / MEMBER
     */
    public static final String CLAIM_USER_TYPE = "userType";

    // ==================== Claim Key ====================
    /**
     * 用户名
     */
    public static final String CLAIM_USERNAME = "username";
    /**
     * 角色
     */
    public static final String CLAIM_ROLES = "roles";
    /**
     * 权限
     */
    public static final String CLAIM_PERMS = "perms";
    /**
     * Token 类型：ACCESS / REFRESH
     */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    /**
     * 会话 ID
     */
    public static final String CLAIM_SESSION_ID = "sessionId";
    /**
     * 认证头
     */
    public static final String HEADER_AUTH = "Authorization";

    // ==================== Header ====================
    /**
     * Bearer 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    private JwtConstants() {
    }

}
