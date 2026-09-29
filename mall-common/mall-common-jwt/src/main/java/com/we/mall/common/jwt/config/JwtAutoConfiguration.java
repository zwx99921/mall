package com.we.mall.common.jwt.config;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.jwt.properties.JwtProperties;
import com.we.mall.common.jwt.provider.JwtServiceProvider;
import com.we.mall.common.jwt.service.JwtService;
import com.we.mall.common.jwt.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Jwt 自动配置
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(Jwts.class)
@ConditionalOnProperty(prefix = "mall.jwt", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({JwtProperties.class})
public class JwtAutoConfiguration {

    /**
     * 管理端 JwtService
     * <p>
     * 仅在配置了 {@code mall.jwt.admin.secret} 时注册。
     */
    @Bean("adminJwtService")
    @ConditionalOnMissingBean(name = "adminJwtService")
    @ConditionalOnProperty(prefix = "mall.jwt.admin", name = "secret")
    public JwtService adminJwtService(JwtProperties jwtProperties) {
        return new JwtServiceImpl(jwtProperties, ClientType.ADMIN);
    }

    /**
     * C 端 JwtService
     * <p>
     * 仅在配置了 {@code mall.jwt.member.secret} 时注册。
     */
    @Bean("memberJwtService")
    @ConditionalOnMissingBean(name = "memberJwtService")
    @ConditionalOnProperty(prefix = "mall.jwt.member", name = "secret")
    public JwtService memberJwtService(JwtProperties jwtProperties) {
        return new JwtServiceImpl(jwtProperties, ClientType.MEMBER);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtServiceProvider jwtServiceProvider(List<JwtService> jwtServices) {
        Map<ClientType, JwtService> map = new EnumMap<>(ClientType.class);
        for (JwtService service : jwtServices) {
            ClientType type = service.getUserType();
            if (type == null) {
                log.warn("JwtService {} 未返回 UserType，已跳过", service.getClass());
                continue;
            }
            map.put(type, service);
        }
        return new JwtServiceProvider(map);
    }

}
