package com.we.mall.common.redis.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.properties.RedisProperties;
import com.we.mall.common.redis.service.RedisCacheService;
import com.we.mall.common.redis.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存读取服务实现
 * <p>
 * 三防说明：
 * 1. 防穿透：loader 返回 null 时，写入 NULL_MARKER，短 TTL
 * 2. 防击穿：未命中时加分布式锁，只放一个请求回源，双重检查
 * 3. 防雪崩：TTL 加随机扰动
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    /**
     * 空值标记
     * <p>
     * 用于区分「缓存了 null」与「缓存未命中」。
     * 使用特殊字符串，避免与业务值冲突。
     */
    private static final String NULL_MARKER = "__NULL__";

    /**
     * 重建锁自动释放时长（秒）
     * <p>
     * -1 表示启用 Redisson看门狗自动续期，适合回源较慢的场景。
     */
    private static final long LOCK_LEASE_SECONDS = -1L;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTypeConvert redisTypeConvert;

    /**
     * Redisson 锁服务，可为 null
     * <p>
     * 为 null 时，getOrLoadWithLock 降级为普通 getOrLoad。
     */
    private final RedisLockService redisLockService;

    /**
     * 配置属性，可为 null
     * <p>
     * 为 null 时使用默认值。
     */
    private final RedisProperties redisProperties;

    // ==================== 防穿透 ====================

    @Override
    public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader, long timeout, TimeUnit unit) {
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return null;
            }
            return redisTypeConvert.convert(cache, clazz);
        }
        T data = loader.get();
        writeCache(key, data, timeout, unit);
        return data;
    }

    @Override
    public <T> T getOrLoad(String key, TypeReference<T> typeRef, Supplier<T> loader, long timeout, TimeUnit unit) {
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return null;
            }
            return redisTypeConvert.convert(cache, typeRef);
        }
        T data = loader.get();
        writeCache(key, data, timeout, unit);
        return data;
    }

    @Override
    public <T> List<T> getOrLoadList(String key, Class<T> elementType, Supplier<List<T>> loader, long timeout, TimeUnit unit) {
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return Collections.emptyList();
            }
            return redisTypeConvert.convertList(cache, elementType);
        }
        List<T> data = loader.get();
        if (data == null || data.isEmpty()) {
            writeNullMarker(key);
            return Collections.emptyList();
        }
        redisTemplate.opsForValue().set(key, data, timeout, unit);
        return data;
    }

    // ==================== 防穿透 + 防击穿 ====================

    @Override
    public <T> T getOrLoadWithLock(String key, Class<T> clazz, Supplier<T> loader, long timeout, TimeUnit unit) {
        // 1. 先查缓存
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return null;
            }
            return redisTypeConvert.convert(cache, clazz);
        }

        // 2. 无锁服务时降级为普通 getOrLoad
        if (redisLockService == null) {
            log.debug("RedisLockService Class<T> 不可用，getOrLoadWithLock 降级为 getOrLoad, key={}", key);
            return getOrLoad(key, clazz, loader, timeout, unit);
        }

        // 3. 加锁重建
        String lockKey = key + ":lock";
        return redisLockService.executeWithLock(lockKey, LOCK_LEASE_SECONDS,
                TimeUnit.SECONDS, () -> {
                    // 双重检查
                    Object second = redisTemplate.opsForValue().get(key);
                    if (second != null) {
                        if (NULL_MARKER.equals(second)) {
                            return null;
                        }
                        return redisTypeConvert.convert(second, clazz);
                    }
                    T data = loader.get();
                    writeCache(key, data, timeout, unit);
                    return data;
                });
    }

    @Override
    public <T> T getOrLoadWithLock(String key, TypeReference<T> typeRef, Supplier<T> loader, long timeout, TimeUnit unit) {
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return null;
            }
            return redisTypeConvert.convert(cache, typeRef);
        }

        if (redisLockService == null) {
            log.debug("RedisLockService TypeReference<T> 不可用，getOrLoadWithLock 降级为 getOrLoad, key={}", key);
            return getOrLoad(key, typeRef, loader, timeout, unit);
        }

        String lockKey = key + ":lock";
        return redisLockService.executeWithLock(lockKey, LOCK_LEASE_SECONDS,
                TimeUnit.SECONDS, () -> {
                    Object second = redisTemplate.opsForValue().get(key);
                    if (second != null) {
                        if (NULL_MARKER.equals(second)) {
                            return null;
                        }
                        return redisTypeConvert.convert(second, typeRef);
                    }
                    T data = loader.get();
                    writeCache(key, data, timeout, unit);
                    return data;
                });
    }

    // ==================== 防雪崩 ====================

    @Override
    public <T> T getOrLoadWithRandomTtl(String key, Class<T> clazz, Supplier<T> loader, long baseSeconds, long randomRange) {
        Object cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            if (NULL_MARKER.equals(cache)) {
                return null;
            }
            return redisTypeConvert.convert(cache, clazz);
        }
        T data = loader.get();
        if (data == null) {
            writeNullMarker(key);
            return null;
        }
        long ttl = baseSeconds + ThreadLocalRandom.current().nextLong(randomRange);
        redisTemplate.opsForValue().set(key, data, ttl, TimeUnit.SECONDS);
        return data;
    }

    // ==================== 缓存失效 ====================

    @Override
    public Boolean evict(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        writeCache(key, value, timeout, unit);
    }

    // ==================== 私有辅助 ====================

    /**
     * 写入缓存
     * <p>
     * data 为 null 时写入空值标记，TTL 走 nullValueTtlSeconds。
     *
     * @param key     缓存 key
     * @param data    值
     * @param timeout 正常值过期时长
     * @param unit    时间单位
     */
    private void writeCache(String key, Object data, long timeout, TimeUnit unit) {
        if (data == null) {
            writeNullMarker(key);
            return;
        }
        redisTemplate.opsForValue().set(key, data, timeout, unit);
    }

    /**
     * 写入空值标记，TTL 取配置或默认 60s
     *
     * @param key 缓存 key
     */
    private void writeNullMarker(String key) {
        long ttl = redisProperties != null
                ? redisProperties.getNullValueTtlSeconds()
                : 60L;
        redisTemplate.opsForValue().set(key, NULL_MARKER, ttl, TimeUnit.SECONDS);
    }

}
