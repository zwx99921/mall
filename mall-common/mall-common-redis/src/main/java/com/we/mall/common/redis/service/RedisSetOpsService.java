package com.we.mall.common.redis.service;

import java.util.Set;

/**
 * Redis Set 类型操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisSetOpsService {

    /**
     * 添加元素
     *
     * @param key    键
     * @param values 元素可变参数
     * @return 实际新增数量
     */
    Long add(String key, Object... values);

    /**
     * 删除元素
     *
     * @param key    键
     * @param values 元素可变参数
     * @return 实际删除数量
     */
    Long remove(String key, Object... values);

    /**
     * 判断元素是否存在
     *
     * @param key   键
     * @param value 元素
     * @return true 存在，false 不存在
     */
    Boolean isMember(String key, Object value);

    /**
     * 获取所有元素并转换为 Set<T>
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 Set；不存在返回空 Set
     */
    <T> Set<T> members(String key, Class<T> clazz);

    /**
     * 获取集合大小
     *
     * @param key 键
     * @return 元素数量
     */
    Long size(String key);

    /**
     * 随机取一个元素并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 随机元素；为空返回 null
     */
    <T> T randomMember(String key, Class<T> clazz);

    /**
     * 求交集并转换
     *
     * @param key      键
     * @param otherKey 另一键
     * @param clazz    元素类型
     * @param <T>      元素泛型
     * @return 交集结果
     */
    <T> Set<T> intersect(String key, String otherKey, Class<T> clazz);

    /**
     * 求并集并转换
     *
     * @param key      键
     * @param otherKey 另一键
     * @param clazz    元素类型
     * @param <T>      元素泛型
     * @return 并集结果
     */
    <T> Set<T> union(String key, String otherKey, Class<T> clazz);

}
