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
     * 认证头
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    private HeaderConstants() {
    }


    // ==================== 客户端内部请求头 ====================
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
     * 内部头：客户端类型
     */
    public static final String HEADER_INTERNAL_CLIENT_TYPE = INTERNAL_HEADER_PREFIX + "Client-Type";
    /**
     * 内部头：会话 ID
     */
    public static final String HEADER_INTERNAL_SESSION_ID = INTERNAL_HEADER_PREFIX + "Session-Id";
    /**
     * 内部头：设备类型
     */
    public static final String HEADER_INTERNAL_DEVICE_TYPE = INTERNAL_HEADER_PREFIX + "Device-Type";
    /**
     * 内部头：服务间调用凭证
     */
    public static final String HEADER_INTERNAL_SERVICE_TOKEN = INTERNAL_HEADER_PREFIX + "Service-Token";

}
