package com.we.mall.common.security.config;

import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.util.JwtUtils;
import com.we.mall.common.security.aspect.PermissionAspect;
import com.we.mall.common.security.authenticator.TokenAuthenticator;
import com.we.mall.common.security.interceptor.AuthInterceptor;
import com.we.mall.common.security.properties.SecurityProperties;
import com.we.mall.common.security.resolver.ClientTypeResolver;
import com.we.mall.common.security.resolver.impl.DefaultClientTypeResolver;
import com.we.mall.common.session.service.SessionService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security 自动配置
 *
 * @author we
 * @date 2026-09-20
 * @description
 */

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "mall.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityAutoConfiguration {


    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 注解式权限校验切面
     */
    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ConditionalOnMissingBean
    public PermissionAspect permissionAspect() {
        return new PermissionAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientTypeResolver clientTypeResolver() {
        return new DefaultClientTypeResolver();
    }

    /**
     * Token 认证器
     * <p>
     * 用 ObjectProvider 注入，任一依赖不存在也能启动。
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(JwtUtils.class)
    public TokenAuthenticator tokenAuthenticator(
            ObjectProvider<SessionService> sessionServiceProvider,
            ObjectProvider<JwtServiceProvider> jwtServiceProvider,
            ClientTypeResolver clientTypeResolver) {
        return new TokenAuthenticator(
                sessionServiceProvider.getIfAvailable(),
                jwtServiceProvider.getIfAvailable(),
                clientTypeResolver);
    }

    /**
     * WebMvc 配置：注册拦截器
     */
    @Configuration
    @ConditionalOnClass(WebMvcConfigurer.class)
    public static class WebMvcConfig implements WebMvcConfigurer {
        private final TokenAuthenticator tokenAuthenticator;
        private final SecurityProperties securityProperties;

        public WebMvcConfig(TokenAuthenticator tokenAuthenticator, SecurityProperties securityProperties) {
            this.tokenAuthenticator = tokenAuthenticator;
            this.securityProperties = securityProperties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            AuthInterceptor interceptor = new AuthInterceptor(
                    tokenAuthenticator,
                    securityProperties.isRequired(),
                    securityProperties.isGatewayMode());

            registry.addInterceptor(interceptor).addPathPatterns("/**")
                    .excludePathPatterns(securityProperties.getExcludePaths());
        }
    }


}
