package com.we.mall.common.rpc.config;

import com.we.mall.common.rpc.interceptor.RpcInboundInterceptor;
import com.we.mall.common.rpc.interceptor.RpcOutboundInterceptor;
import com.we.mall.common.rpc.properties.RpcProperties;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
     * 出站拦截器：带服务凭证
     */
    @Configuration
    @ConditionalOnClass(RequestInterceptor.class)
    public static class OutboundConfig {

        @Bean
        @ConditionalOnMissingBean
        public RpcOutboundInterceptor rpcOutboundInterceptor(RpcProperties rpcProperties) {
            return new RpcOutboundInterceptor(rpcProperties.getServiceToken());
        }
    }

    /**
     * 入站拦截器：校验服务凭证
     */
    @Configuration
    @ConditionalOnClass(WebMvcConfigurer.class)
    public static class InboundConfig implements WebMvcConfigurer {

        private final RpcProperties rpcProperties;

        public InboundConfig(RpcProperties rpcProperties) {
            this.rpcProperties = rpcProperties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new RpcInboundInterceptor(rpcProperties.getServiceToken()))
                    .addPathPatterns(rpcProperties.getInternalPaths());
        }
    }

}
