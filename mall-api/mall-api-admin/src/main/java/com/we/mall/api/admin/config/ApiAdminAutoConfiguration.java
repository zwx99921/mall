package com.we.mall.api.admin.config;

import com.we.mall.api.admin.factory.LoginLogFeignFallback;
import com.we.mall.api.admin.factory.UserFeignFallback;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 自动配置
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@AutoConfiguration
public class ApiAdminAutoConfiguration {

    /**
     * 注册 FallbackFactory
     * <p>
     * 仅在调用方没有自定义 Fallback 时注册。
     */
    @Bean
    @ConditionalOnMissingBean
    public UserFeignFallback userFeignFallback() {
        return new UserFeignFallback();
    }

    /**
     * 注册 FallbackFactory
     * <p>
     * 仅在调用方没有自定义 Fallback 时注册。
     */
    @Bean
    @ConditionalOnMissingBean
    public LoginLogFeignFallback loginLogFeignFallback() {
        return new LoginLogFeignFallback();
    }

}
