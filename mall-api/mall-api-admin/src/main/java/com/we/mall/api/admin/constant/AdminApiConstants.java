package com.we.mall.api.admin.constant;

/**
 * Admin 服务 API 常量
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
public interface AdminApiConstants {

    /**
     * 服务名（Nacos 注册名）
     */
    String SERVICE_NAME = "mall-modules-admin";

    String USER_CONTEXT_ID = "userFeignClient";
    String INNER_USER_PREFIX = "/inner/user";

    String LOGIN_LOG_CONTEXT_ID = "loginLogFeignClient";
    String INNER_LOGIN_LOG_PREFIX = "/inner/loginLog";

}
