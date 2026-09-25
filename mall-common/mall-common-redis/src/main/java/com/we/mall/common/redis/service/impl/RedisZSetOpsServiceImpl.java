package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.model.ScoredValue;
import com.we.mall.common.redis.service.RedisZSetOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Redis ZSet（有序集合）操作封装
 * <p>
 * 适合做排行榜、延时队列、带权重的排序。
 * 读取方法返回类型安全的 List<T> 或 List<ScoredValue<T>>。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisZSetOpsServiceImpl implements RedisZSetOpsService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTypeConvert redisTypeConvert;

    /**
     * 添加元素及分数
     *
     * @param key   键
     * @param value 元素
     * @param score 分数
     * @return true 新增，false 更新
     */
    @Override
    public Boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 删除元素
     *
     * @param key    键
     * @param values 元素可变参数
     * @return 实际删除数量
     */
    @Override
    public Long remove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素分数
     *
     * @param key   键
     * @param value 元素
     * @param delta 增量
     * @return 增加后的分数
     */
    @Override
    public Double incrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 获取元素分数
     *
     * @param key   键
     * @param value 元素
     * @return 分数；不存在返回 null
     */
    @Override
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取元素正序排名（从 0 开始）
     *
     * @param key   键
     * @param value 元素
     * @return 排名；不存在返回 null
     */
    @Override
    public Long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取元素倒序排名（从 0 开始）
     *
     * @param key   键
     * @param value 元素
     * @return 排名；不存在返回 null
     */
    @Override
    public Long reverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

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
    @Override
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        Set<Object> raw = redisTemplate.opsForZSet().range(key, start, end);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }

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
    @Override
    public <T> List<ScoredValue<T>> rangeWithScores(String key, long start, long end, Class<T> clazz) {
        Set<ZSetOperations.TypedTuple<Object>> raw = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScoredValue<T>> result = new ArrayList<>(raw.size());
        for (ZSetOperations.TypedTuple<Object> tuple : raw) {
            result.add(new ScoredValue<>(
                    redisTypeConvert.convert(tuple.getValue(), clazz),
                    tuple.getScore() == null ? 0 : tuple.getScore()));
        }
        return result;
    }

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
    @Override
    public <T> List<T> rangeByScore(String key, double min, double max, Class<T> clazz) {
        Set<Object> raw = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }

    /**
     * 统计分数区间内的元素数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    @Override
    public Long count(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key 键
     * @return 元素数量
     */
    @Override
    public Long size(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
