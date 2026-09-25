package com.we.mall.common.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存读取服务接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisCacheService {

    // ==================== 防穿透 ====================

    /**
     * 读取缓存，未命中回源并写缓存（防穿透：空值缓存）
     *
     * @param key     缓存 key
     * @param clazz   目标类型
     * @param loader  回源加载器
     * @param timeout 正常值过期时长
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 缓存值或回源值；loader 返回 null 时返回 null
     */
    <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader, long timeout, TimeUnit unit);

    /**
     * 读取缓存，未命中回源并写缓存（防穿透：空值缓存），支持泛型对象
     *
     * @param key     缓存 key
     * @param typeRef 泛型引用，如 new TypeReference&lt;Result&lt;User&gt;&gt;() {}
     * @param loader  回源加载器
     * @param timeout 正常值过期时长
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 缓存值或回源值；loader 返回 null 时返回 null
     */
    <T> T getOrLoad(String key, TypeReference<T> typeRef, Supplier<T> loader, long timeout, TimeUnit unit);

    /**
     * 读取缓存，未命中回源并写缓存（防穿透：空值缓存），支持 List
     *
     * @param key         缓存 key
     * @param elementType 元素类型
     * @param loader      回源加载器
     * @param timeout     正常值过期时长
     * @param unit        时间单位
     * @param <T>         元素泛型
     * @return 缓存值或回源值；loader 返回 null 时返回空 List
     */
    <T> List<T> getOrLoadList(String key, Class<T> elementType, Supplier<List<T>> loader, long timeout, TimeUnit unit);

    // ==================== 防穿透 + 防击穿 ====================

    /**
     * 读取缓存，未命中加锁回源（防穿透 + 防击穿）
     * <p>
     * 加锁失败时抛 IllegalStateException。
     *
     * @param key     缓存 key
     * @param clazz   目标类型
     * @param loader  回源加载器
     * @param timeout 正常值过期时长
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 缓存值或回源值
     */
    <T> T getOrLoadWithLock(String key, Class<T> clazz, Supplier<T> loader, long timeout, TimeUnit unit);

    /**
     * 读取缓存，未命中加锁回源（防穿透 + 防击穿），支持泛型对象
     *
     * @param key     缓存 key
     * @param typeRef 泛型引用
     * @param loader  回源加载器
     * @param timeout 正常值过期时长
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 缓存值或回源值
     */
    <T> T getOrLoadWithLock(String key, TypeReference<T> typeRef, Supplier<T> loader, long timeout, TimeUnit unit);

    // ==================== 防雪崩 ====================

    /**
     * 读取缓存，未命中回源并写缓存，TTL 带随机扰动（防雪崩）
     *
     * @param key         缓存 key
     * @param clazz       目标类型
     * @param loader      回源加载器
     * @param baseSeconds 基础过期秒数
     * @param randomRange 随机扰动范围（秒），实际 TTL = base + [0, randomRange)
     * @param <T>         泛型
     * @return 缓存值或回源值
     */
    <T> T getOrLoadWithRandomTtl(String key, Class<T> clazz, Supplier<T> loader, long baseSeconds, long randomRange);

    // ==================== 缓存失效 ====================

    /**
     * 删除缓存
     *
     * @param key 缓存 key
     * @return true 删除成功，false 不存在
     */
    Boolean evict(String key);

    /**
     * 写入缓存
     *
     * @param key     缓存 key
     * @param value   值
     * @param timeout 过期时长
     * @param unit    时间单位
     */
    void put(String key, Object value, long timeout, TimeUnit unit);

}
