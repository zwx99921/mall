package com.we.mall.common.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis String 类型操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisStringOpsService {

    // ==================== 写 ====================

    /**
     * 设置值（无过期时间，永久有效）
     *
     * @param key   键
     * @param value 值，任意对象，最终以 JSON 存储
     */
    void set(String key, Object value);

    /**
     * 设置值并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 时长
     * @param unit    时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 设置值并使用「基础 TTL + 随机扰动」的过期时间，用于防止缓存雪崩
     *
     * @param key         键
     * @param value       值
     * @param baseSeconds 基础过期秒数
     * @param randomRange 随机扰动范围（秒），实际 TTL = base + [0, randomRange)
     */
    void setWithRandomTtl(String key, Object value, long baseSeconds, long randomRange);

    /**
     * 仅当 key 不存在时设置值（分布式锁常用）
     *
     * @param key     键
     * @param value   值
     * @param timeout 时长
     * @param unit    时间单位
     * @return true 设置成功（之前不存在），false 已存在未设置
     */
    Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 仅当 key 存在时设置值
     *
     * @param key   键
     * @param value 值
     * @return true 设置成功，false key 不存在
     */
    Boolean setIfPresent(String key, Object value);

    /**
     * 批量设置键值对
     *
     * @param map 键值映射
     */
    void multiSet(Map<String, Object> map);

    // ==================== 读（安全转换） ====================

    /**
     * 读取单个对象并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象；不存在返回 null
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 读取泛型对象（如 Result<User>）
     *
     * @param key     键
     * @param typeRef 泛型引用
     * @param <T>     泛型
     * @return 转换后的对象；不存在返回 null
     */
    <T> T get(String key, TypeReference<T> typeRef);

    /**
     * 读取 List 并转换为 List<T>
     *
     * @param key         键
     * @param elementType 元素类型
     * @param <T>         元素泛型
     * @return 转换后的 List；不存在返回空 List
     */
    <T> List<T> getList(String key, Class<T> elementType);

    /**
     * 读取 Set 并转换为 Set<T>
     *
     * @param key         键
     * @param elementType 元素类型
     * @param <T>         元素泛型
     * @return 转换后的 Set；不存在返回空 Set
     */
    <T> Set<T> getSet(String key, Class<T> elementType);

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
    <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType);

    /**
     * 批量读取多个 key 并转换为 List<T>
     *
     * @param keys  键集合
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List，顺序与传入 keys 一致
     */
    <T> List<T> multiGet(Collection<String> keys, Class<T> clazz);

    // ==================== 数值 ====================

    /**
     * 自增（长整型）
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    Long increment(String key, long delta);

    /**
     * 自增（浮点型）
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    Double increment(String key, double delta);

    /**
     * 自减（长整型）
     *
     * @param key   键
     * @param delta 减量
     * @return 自减后的值
     */
    Long decrement(String key, long delta);

}
