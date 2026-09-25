package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.service.RedisHashOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * Redis Hash 类型操作封装实现
 * <p>
 * 适合存储对象字段（如用户信息、配置项）。
 * 读取方法统一通过 RedisTypeConverter 做类型安全转换。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisHashOpsServiceImpl implements RedisHashOpsService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTypeConvert redisTypeConvert;

    // ==================== 写 ====================

    /**
     * 设置单个 field
     *
     * @param key   hash 键
     * @param field 字段名
     * @param value 字段值
     */
    @Override
    public void put(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 批量设置 field
     *
     * @param key hash 键
     * @param map 字段映射
     */
    @Override
    public void putAll(String key, Map<? extends String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 仅当 field 不存在时设置
     *
     * @param key   hash 键
     * @param field 字段名
     * @param value 字段值
     * @return true 设置成功，false 已存在
     */
    @Override
    public Boolean putIfAbsent(String key, String field, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }

    // ==================== 读（安全转换） ====================

    /**
     * 读取单个 field 并转换为指定类型
     *
     * @param key   hash 键
     * @param field 字段名
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的值；不存在返回 null
     */
    @Override
    public <T> T get(String key, String field, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForHash().get(key, field), clazz);
    }

    /**
     * 读取整个 hash 并转换为 Map<String, V>
     *
     * @param key       hash 键
     * @param valueType value 类型
     * @param <V>       value 泛型
     * @return 转换后的 Map；不存在返回空 Map
     */
    @Override
    public <V> Map<String, V> getAll(String key, Class<V> valueType) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, V> result = new LinkedHashMap<>(entries.size());
        entries.forEach((k, v) -> result.put(String.valueOf(k),
                redisTypeConvert.convert(v, valueType)));
        return result;
    }

    /**
     * 批量读取多个 field 并转换为 List<V>
     *
     * @param key       hash 键
     * @param fields    字段集合
     * @param valueType value 类型
     * @param <V>       value 泛型
     * @return 转换后的 List，顺序与传入 fields 一致
     */
    @Override
    public <V> List<V> multiGet(String key, Collection<String> fields, Class<V> valueType) {
        List<Object> raw = redisTemplate.opsForHash()
                .multiGet(key, new ArrayList<>(fields));
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<V> result = new ArrayList<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, valueType));
        }
        return result;
    }

    /**
     * 获取 hash 中所有 field 名
     *
     * @param key hash 键
     * @return field 集合
     */
    @Override
    public Set<String> keys(String key) {
        Set<Object> raw = redisTemplate.opsForHash().keys(key);
        if (raw.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>(raw.size());
        for (Object o : raw) {
            result.add(String.valueOf(o));
        }
        return result;
    }

    /**
     * 获取 hash 中 field 数量
     *
     * @param key hash 键
     * @return 数量
     */
    @Override
    public Long size(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 判断 field 是否存在
     *
     * @param key   hash 键
     * @param field 字段名
     * @return true 存在，false 不存在
     */
    @Override
    public Boolean hasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== 删 ====================

    /**
     * 删除一个或多个 field
     *
     * @param key    hash 键
     * @param fields 字段名可变参数
     * @return 实际删除数量
     */
    @Override
    public Long delete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    // ==================== 数值 ====================

    /**
     * field 值自增（长整型）
     *
     * @param key   hash 键
     * @param field 字段名
     * @param delta 增量
     * @return 自增后的值
     */
    @Override
    public Long increment(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * field 值自增（浮点型）
     *
     * @param key   hash 键
     * @param field 字段名
     * @param delta 增量
     * @return 自增后的值
     */
    @Override
    public Double increment(String key, String field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }
}
