package com.we.mall.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.properties.RedisProperties;
import com.we.mall.common.redis.service.*;
import com.we.mall.common.redis.service.impl.*;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Redis 自动配置
 * <p>
 * 统一注册：
 * 1. RedisTemplate（序列化配置）
 * 2. redisObjectMapper（专用 ObjectMapper）
 * 3. RedisTypeConverter（类型安全转换）
 * 4. 6 个 Ops 实现类
 * 5. RedissonClient + RedisLockService（可选）
 *
 * @author we
 * @date 2026-09-20
 * @description
 */

@AutoConfiguration(before = RedisAutoConfiguration.class)
@EnableConfigurationProperties({RedisProperties.class})
public class MallRedisAutoConfiguration {

    // ==================== ObjectMapper ====================
    @Bean("redisObjectMapper")
    @ConditionalOnMissingBean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // ==================== Java 8 时间支持 ====================

        // 注册 JavaTimeModule（LocalDate、LocalDateTime、LocalTime、Duration 等）
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        mapper.registerModule(javaTimeModule);

        // 关闭时间戳输出（用 ISO 字符串或自定义格式）
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // ==================== 字段可见性 ====================
        // 字段可见性
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // ==================== 忽略未知字段 ====================
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // ==================== 多态类型 ====================
        // 安全的多态类型校验
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.we.mall.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.time.")
                .allowIfSubType("java.lang.")
                .build();

        mapper.activateDefaultTyping(
                validator,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    // ==================== RedisTemplate ====================

    /**
     * RedisTemplate 序列化配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @ConditionalOnProperty(prefix = "mall.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    // ==================== RedisTypeConverter ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(name = "redisObjectMapper")
    public RedisTypeConvert redisTypeConverter(@Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        return new RedisTypeConvert(redisObjectMapper);
    }

    // ==================== Ops 实现注册 ====================
    // 全部依赖 RedisTemplate + RedisTypeConverter

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisKeyOpsService redisKeyOpsService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisKeyOpsServiceImpl(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisStringOpsService redisStringOpsService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert) {
        return new RedisStringOpsServiceImpl(redisTemplate, redisTypeConvert);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisHashOpsService redisHashOpsService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert) {
        return new RedisHashOpsServiceImpl(redisTemplate, redisTypeConvert);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisListOpsService redisListOpsService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert) {
        return new RedisListOpsServiceImpl(redisTemplate, redisTypeConvert);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisSetOpsService redisSetOpsService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert) {
        return new RedisSetOpsServiceImpl(redisTemplate, redisTypeConvert);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisZSetOpsService redisZSetOpsService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert) {
        return new RedisZSetOpsServiceImpl(redisTemplate, redisTypeConvert);
    }

    // ==================== Pipeline 实现注册 ====================

    /**
     * 注册 RedisPipelineService
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisPipelineService redisPipelineService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisPipelineServiceImpl(redisTemplate);
    }

    // ==================== Redisson ====================

    /**
     * RedissonClient
     * <p>
     * 仅在 mall.redis.redisson.enabled=true 时注册。
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "mall.redis.redisson", name = "enabled", havingValue = "true")
    public RedissonClient redissonClient(RedisProperties redisProperties) {

        RedisProperties.RedissonProperties redisson = redisProperties.getRedisson();


        Config config = new Config();
        String address = "redis://" + redisson.getHost() + ":" + redisson.getPort();

        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisson.getDatabase())
                .setPassword(StringUtils.hasText(redisson.getPassword()) ? redisson.getPassword() : null)
                .setConnectionPoolSize(redisson.getPoolSize())
                .setConnectionMinimumIdleSize(redisson.getMinIdleSize());

        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean
    public RedisLockService redisLockService(RedissonClient redissonClient) {
        return new RedisLockServiceImpl(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheService.class)
    @ConditionalOnBean({RedisTemplate.class, RedisTypeConvert.class})
    public RedisCacheService redisCacheService(
            RedisTemplate<String, Object> redisTemplate,
            RedisTypeConvert redisTypeConvert,
            RedisProperties redisProperties,
            ObjectProvider<RedisLockService> redisLockServiceProvider) {
        RedisLockService lockService = redisLockServiceProvider.getIfAvailable();
        return new RedisCacheServiceImpl(redisTemplate, redisTypeConvert, lockService, redisProperties);
    }

}
