package com.we.mall.common.redis.service;

import com.we.mall.common.redis.model.ScoredValue;

import java.util.List;

/**
 * Redis ZSet（有序集合）操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisZSetOpsService {

    /**
     * 添加元素及分数
     *
     * @param key   键
     * @param value 元素
     * @param score 分数
     * @return true 新增，false 更新
     */
    Boolean add(String key, Object value, double score);

    /**
     * 删除元素
     *
     * @param key    键
     * @param values 元素可变参数
     * @return 实际删除数量
     */
    Long remove(String key, Object... values);

    /**
     * 增加元素分数
     *
     * @param key   键
     * @param value 元素
     * @param delta 增量
     * @return 增加后的分数
     */
    Double incrementScore(String key, Object value, double delta);

    /**
     * 获取元素分数
     *
     * @param key   键
     * @param value 元素
     * @return 分数；不存在返回 null
     */
    Double score(String key, Object value);

    /**
     * 获取元素正序排名（从 0 开始）
     *
     * @param key   键
     * @param value 元素
     * @return 排名；不存在返回 null
     */
    Long rank(String key, Object value);

    /**
     * 获取元素倒序排名（从 0 开始）
     *
     * @param key   键
     * @param value 元素
     * @return 排名；不存在返回 null
     */
    Long reverseRank(String key, Object value);

    /**
     * 按排名区间取元素（正序）并转换
     *
     * @param key   键
     * @param start 起始排名（含）
     * @param end   结束排名（含），-1 表示末尾
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List；不存在返回空 List
     */
    <T> List<T> range(String key, long start, long end, Class<T> clazz);

    /**
     * 按排名区间取元素及分数（正序）
     *
     * @param key   键
     * @param start 起始排名（含）
     * @param end   结束排名（含）
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 带分数的结果列表
     */
    <T> List<ScoredValue<T>> rangeWithScores(String key, long start, long end, Class<T> clazz);

    /**
     * 按分数区间取元素（正序）并转换
     *
     * @param key   键
     * @param min   最小分数（含）
     * @param max   最大分数（含）
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List
     */
    <T> List<T> rangeByScore(String key, double min, double max, Class<T> clazz);

    /**
     * 统计分数区间内的元素数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    Long count(String key, double min, double max);

    /**
     * 获取集合大小
     *
     * @param key 键
     * @return 元素数量
     */
    Long size(String key);


}
