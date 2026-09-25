package com.we.mall.gateway.config;

import com.we.mall.gateway.properties.GatewayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway 配置类
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfig {
}
