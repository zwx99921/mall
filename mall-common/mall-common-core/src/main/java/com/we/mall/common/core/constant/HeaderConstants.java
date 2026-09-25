package com.we.mall.common.core.constant;

/**
 * 请求头 常量
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class HeaderConstants {

    /**
     * 客户端请求头：客户端类型，取值 admin / member
     */
    public static final String HEADER_CLIENT_TYPE = "X-Client-Type";

    // ==================== 客户端请求头 ====================
    /**
     * 客户端请求头：设备名称
     */
    public static final String HEADER_DEVICE_NAME = "X-Device-Name";
    /**
     * 内部请求头前缀，避免和业务参数冲突
     */
    public static final String INTERNAL_HEADER_PREFIX = "X-Internal-";

    // ==================== 网关下发的内部请求头 ====================
    /**
     * 内部头：用户 ID
     */
    public static final String HEADER_INTERNAL_USER_ID = INTERNAL_HEADER_PREFIX + "User-Id";
    /**
     * 内部头：用户名
     */
    public static final String HEADER_INTERNAL_USERNAME = INTERNAL_HEADER_PREFIX + "Username";
    /**
     * 内部头：昵称
     */
    public static final String HEADER_INTERNAL_NICKNAME = INTERNAL_HEADER_PREFIX + "Nickname";
    /**
     * 内部头：租户 ID
     */
    public static final String HEADER_INTERNAL_TENANT_ID = INTERNAL_HEADER_PREFIX + "Tenant-Id";
    /**
     * 内部头：客户端类型
     */
    public static final String HEADER_INTERNAL_CLIENT_TYPE = INTERNAL_HEADER_PREFIX + "Client-Type";
    /**
     * 内部头：会话 ID
     */
    public static final String HEADER_INTERNAL_SESSION_ID = INTERNAL_HEADER_PREFIX + "Session-Id";
    /**
     * 内部头：角色，多个用逗号分隔
     */
    public static final String HEADER_INTERNAL_ROLES = INTERNAL_HEADER_PREFIX + "Roles";
    /**
     * 内部头：权限，多个用逗号分隔
     */
    public static final String HEADER_INTERNAL_PERMS = INTERNAL_HEADER_PREFIX + "Perms";
    /**
     * 内部头：设备类型
     */
    public static final String HEADER_INTERNAL_DEVICE_TYPE = INTERNAL_HEADER_PREFIX + "Device-Type";

    private HeaderConstants() {
    }

}
