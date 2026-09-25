package com.we.mall.common.sensitive.config;

import com.we.mall.common.sensitive.interceptor.SensitiveSceneInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 敏感信息自动配置
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@AutoConfiguration
public class SensitiveAutoConfiguration {

    @Bean
    public SensitiveSceneInterceptor sensitiveSceneInterceptor() {
        return new SensitiveSceneInterceptor();
    }

    @Bean
    public WebMvcConfigurer sensitiveWebMvcConfigurer(SensitiveSceneInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor);
            }
        };
    }

}
