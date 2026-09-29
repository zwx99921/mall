package com.we.mall.common.jwt.constant;

/**
 * JWT 常量
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public final class JwtConstants {

    /**
     * 客户端类型：ADMIN / MEMBER
     */
    public static final String CLAIM_CLIENT_TYPE = "clientType";

    // ==================== Claim Key ====================
    /**
     * 用户名
     */
    public static final String CLAIM_USERNAME = "username";

    private JwtConstants() {
    }
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
    // ==================== Header ====================
    /**
     * Bearer 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

}
