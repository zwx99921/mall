package com.we.mall.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 安全配置
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.security")
public class SecurityProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 是否强制登录
     * <p>
     * true：无 token 直接抛 401
     * false：无 token 放行，由业务自行判断
     */
    private boolean required = false;

    /**
     * 是否从网关 header 还原上下文
     * <p>
     * true：从 X-User-Id / X-Username 等 header 还原（网关已认证）
     * false：从 Authorization token 解析（服务自己认证）
     */
    private boolean gatewayMode = true;

    /**
     * 排除的路径（不走认证）
     */
    private List<String> excludePaths = new ArrayList<>(Arrays.asList("/inner/**", "/error"));

    /**
     * 服务间调用凭证
     * <p>
     * 用于 /inner/** 接口的服务身份校验。
     * 生产环境建议放配置中心。
     */
    private String serviceToken;
}
