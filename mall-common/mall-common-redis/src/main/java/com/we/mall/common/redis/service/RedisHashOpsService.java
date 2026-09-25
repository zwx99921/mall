package com.we.mall.common.redis.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis Hash 类型操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisHashOpsService {

    // ==================== 写 ====================

    /**
     * 设置单个 field
     *
     * @param key   hash 键
     * @param field 字段名
     * @param value 字段值
     */
    void put(String key, String field, Object value);

    /**
     * 批量设置 field
     *
     * @param key hash 键
     * @param map 字段映射
     */
    void putAll(String key, Map<? extends String, ?> map);

    /**
     * 仅当 field 不存在时设置
     *
     * @param key   hash 键
     * @param field 字段名
     * @param value 字段值
     * @return true 设置成功，false 已存在
     */
    Boolean putIfAbsent(String key, String field, Object value);

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
    <T> T get(String key, String field, Class<T> clazz);

    /**
     * 读取整个 hash 并转换为 Map<String, V>
     *
     * @param key       hash 键
     * @param valueType value 类型
     * @param <V>       value 泛型
     * @return 转换后的 Map；不存在返回空 Map
     */
    <V> Map<String, V> getAll(String key, Class<V> valueType);

    /**
     * 批量读取多个 field 并转换为 List<V>
     *
     * @param key       hash 键
     * @param fields    字段集合
     * @param valueType value 类型
     * @param <V>       value 泛型
     * @return 转换后的 List，顺序与传入 fields 一致
     */
    <V> List<V> multiGet(String key, Collection<String> fields, Class<V> valueType);

    /**
     * 获取 hash 中所有 field 名
     *
     * @param key hash 键
     * @return field 集合
     */
    Set<String> keys(String key);

    /**
     * 获取 hash 中 field 数量
     *
     * @param key hash 键
     * @return 数量
     */
    Long size(String key);

    /**
     * 判断 field 是否存在
     *
     * @param key   hash 键
     * @param field 字段名
     * @return true 存在，false 不存在
     */
    Boolean hasKey(String key, String field);

    // ==================== 删 ====================

    /**
     * 删除一个或多个 field
     *
     * @param key    hash 键
     * @param fields 字段名可变参数
     * @return 实际删除数量
     */
    Long delete(String key, Object... fields);

    // ==================== 数值 ====================

    /**
     * field 值自增（长整型）
     *
     * @param key   hash 键
     * @param field 字段名
     * @param delta 增量
     * @return 自增后的值
     */
    Long increment(String key, String field, long delta);

    /**
     * field 值自增（浮点型）
     *
     * @param key   hash 键
     * @param field 字段名
     * @param delta 增量
     * @return 自增后的值
     */
    Double increment(String key, String field, double delta);

}
