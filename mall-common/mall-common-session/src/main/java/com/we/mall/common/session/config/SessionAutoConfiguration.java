package com.we.mall.common.session.config;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisSetOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.session.properties.SessionProperties;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.common.session.service.impl.RedisSessionService;
import com.we.mall.common.session.store.SessionIndexStore;
import com.we.mall.common.session.store.SessionInfoStore;
import com.we.mall.common.session.store.SessionUserStore;
import com.we.mall.common.session.store.impl.RedisSessionIndexStore;
import com.we.mall.common.session.store.impl.RedisSessionInfoStore;
import com.we.mall.common.session.store.impl.RedisSessionUserStore;
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

    // ==================== SessionInfoStore ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisStringOpsService.class, RedisKeyOpsService.class})
    public SessionInfoStore sessionInfoStore(RedisStringOpsService redisStringOpsService,
                                             RedisKeyOpsService redisKeyOpsService,
                                             SessionProperties sessionProperties) {
        return new RedisSessionInfoStore(redisStringOpsService, redisKeyOpsService, sessionProperties);
    }

    // ==================== SessionIndexStore ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisSetOpsService.class, RedisKeyOpsService.class})
    public SessionIndexStore sessionIndexStore(RedisSetOpsService redisSetOpsService,
                                               RedisKeyOpsService redisKeyOpsService,
                                               SessionProperties sessionProperties) {
        return new RedisSessionIndexStore(redisSetOpsService, redisKeyOpsService, sessionProperties);
    }

    // ==================== SessionUserStore ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisStringOpsService.class, RedisKeyOpsService.class})
    public SessionUserStore sessionUserStore(RedisStringOpsService redisStringOpsService,
                                             RedisKeyOpsService redisKeyOpsService,
                                             SessionProperties sessionProperties) {
        return new RedisSessionUserStore(redisStringOpsService, redisKeyOpsService, sessionProperties);
    }

    // ==================== SessionService ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({SessionInfoStore.class, SessionIndexStore.class, SessionUserStore.class})
    public SessionService sessionService(SessionInfoStore sessionInfoStore,
                                         SessionIndexStore sessionIndexStore,
                                         SessionUserStore sessionUserStore,
                                         SessionProperties sessionProperties) {
        return new RedisSessionService(sessionInfoStore, sessionIndexStore,
                sessionUserStore, sessionProperties);
    }

}
