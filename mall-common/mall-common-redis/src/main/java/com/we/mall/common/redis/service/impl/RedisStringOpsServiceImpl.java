package com.we.mall.common.redis.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.service.RedisStringOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Redis String 类型操作封装
 * <p>
 * 覆盖写、读、数值增减、批量操作，所有读取方法均返回类型安全的结果。
 * 支持单对象、泛型对象、List、Set、Map 五类读取方式。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisStringOpsServiceImpl implements RedisStringOpsService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTypeConvert redisTypeConvert;

    /**
     * 设置值（无过期时间，永久有效）
     *
     * @param key   键
     * @param value 值，任意对象，最终以 JSON 存储
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 时长
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置值并使用「基础 TTL + 随机扰动」的过期时间，用于防止缓存雪崩
     *
     * @param key         键
     * @param value       值
     * @param baseSeconds 基础过期秒数
     * @param randomRange 随机扰动范围（秒），实际 TTL = base + [0, randomRange)
     */
    @Override
    public void setWithRandomTtl(String key, Object value, long baseSeconds, long randomRange) {
        long ttl = baseSeconds + ThreadLocalRandom.current().nextLong(randomRange);
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    /**
     * 仅当 key 不存在时设置值（分布式锁常用）
     *
     * @param key     键
     * @param value   值
     * @param timeout 时长
     * @param unit    时间单位
     * @return true 设置成功（之前不存在），false 已存在未设置
     */
    @Override
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 仅当 key 存在时设置值
     *
     * @param key   键
     * @param value 值
     * @return true 设置成功，false key 不存在
     */
    @Override
    public Boolean setIfPresent(String key, Object value) {
        return redisTemplate.opsForValue().setIfPresent(key, value);
    }

    /**
     * 批量设置键值对
     *
     * @param map 键值映射
     */
    @Override
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    // ==================== 读（安全转换） ====================

    /**
     * 读取单个对象并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象；不存在返回 null
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForValue().get(key), clazz);
    }

    /**
     * 读取泛型对象（如 Result<User>）
     *
     * @param key     键
     * @param typeRef 泛型引用
     * @param <T>     泛型
     * @return 转换后的对象；不存在返回 null
     */
    @Override
    public <T> T get(String key, TypeReference<T> typeRef) {
        return redisTypeConvert.convert(redisTemplate.opsForValue().get(key), typeRef);
    }

    /**
     * 读取 List 并转换为 List<T>
     *
     * @param key         键
     * @param elementType 元素类型
     * @param <T>         元素泛型
     * @return 转换后的 List；不存在返回空 List
     */
    @Override
    public <T> List<T> getList(String key, Class<T> elementType) {
        return redisTypeConvert.convertList(redisTemplate.opsForValue().get(key), elementType);
    }

    /**
     * 读取 Set 并转换为 Set<T>
     *
     * @param key         键
     * @param elementType 元素类型
     * @param <T>         元素泛型
     * @return 转换后的 Set；不存在返回空 Set
     */
    @Override
    public <T> Set<T> getSet(String key, Class<T> elementType) {
        return redisTypeConvert.convertSet(redisTemplate.opsForValue().get(key), elementType);
    }

    /**
     * 读取 Map 并转换为 Map<K, V>
     *
     * @param key       键
     * @param keyType   key 类型
     * @param valueType value 类型
     * @param <K>       key 泛型
     * @param <V>       value 泛型
     * @return 转换后的 Map；不存在返回空 Map
     */
    @Override
    public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
        return redisTypeConvert.convertMap(redisTemplate.opsForValue().get(key), keyType, valueType);
    }

    /**
     * 批量读取多个 key 并转换为 List<T>
     *
     * @param keys  键集合
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List，顺序与传入 keys 一致
     */
    @Override
    public <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
        List<Object> raw = redisTemplate.opsForValue().multiGet(keys);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }

    // ==================== 数值 ====================

    /**
     * 自增（长整型）
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    @Override
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自增（浮点型）
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    @Override
    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减（长整型）
     *
     * @param key   键
     * @param delta 减量
     * @return 自减后的值
     */
    @Override
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}
