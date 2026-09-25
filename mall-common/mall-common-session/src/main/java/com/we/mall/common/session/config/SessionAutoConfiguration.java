package com.we.mall.common.session.config;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisSetOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.common.session.service.impl.RedisSessionServiceImpl;
import com.we.mall.common.session.store.SessionStore;
import com.we.mall.common.session.store.impl.RedisSessionStoreImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Session 自动配置
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@AutoConfiguration
@ConditionalOnClass({RedisStringOpsService.class, RedisKeyOpsService.class})
@EnableConfigurationProperties(SessionProperties.class)
@ConditionalOnProperty(prefix = "mall.session", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SessionAutoConfiguration {

    /**
     * 默认 Redis 存储，用户可自定义 SessionStore 覆盖
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({
            RedisStringOpsService.class,
            RedisSetOpsService.class,
            RedisKeyOpsService.class
    })
    public SessionStore sessionStore(RedisStringOpsService redisStringOpsService,
                                     RedisSetOpsService redisSetOpsService,
                                     RedisKeyOpsService redisKeyOpsService,
                                     SessionProperties sessionProperties) {
        return new RedisSessionStoreImpl(redisStringOpsService, redisSetOpsService, redisKeyOpsService, sessionProperties);
    }

    /**
     * 默认会话服务，用户可自定义 SessionService 覆盖
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SessionStore.class)
    public SessionService sessionService(SessionStore store, SessionProperties sessionProperties) {
        return new RedisSessionServiceImpl(store, sessionProperties);
    }

}
