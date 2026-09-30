package com.we.mall.common.rpc.config;

import com.we.mall.common.rpc.filter.ServiceTokenFilter;
import com.we.mall.common.rpc.interceptor.ServiceTokenFeignInterceptor;
import com.we.mall.common.rpc.properties.RpcProperties;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务间通信自动配置
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@AutoConfiguration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcAutoConfiguration {

    /**
     * 入站校验 Filter（对 Controller 和 Actuator 端点都生效）
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceTokenFilter serviceTokenFilter(RpcProperties rpcProperties) {
        return new ServiceTokenFilter(rpcProperties);
    }

    /**
     * 出站拦截器：带服务凭证
     */
    @Configuration
    @ConditionalOnClass(RequestInterceptor.class)
    public static class OutboundConfig {

        @Bean
        @ConditionalOnMissingBean
        public ServiceTokenFeignInterceptor serviceTokenFeignInterceptor(RpcProperties rpcProperties) {
            return new ServiceTokenFeignInterceptor(rpcProperties.getServiceToken());
        }
    }

}
