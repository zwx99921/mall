package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.service.RedisListOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Redis List 类型操作封装实现
 * <p>
 * 适合做队列、栈、时间线。读取方法返回类型安全的 List<T>。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisListOpsServiceImpl implements RedisListOpsService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTypeConvert redisTypeConvert;

    // ==================== 写 ====================

    /**
     * 左侧入队（栈顶）
     *
     * @param key   键
     * @param value 值
     * @return 入队后列表长度
     */
    @Override
    public Long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 批量左侧入队
     *
     * @param key    键
     * @param values 值集合
     * @return 入队后列表长度
     */
    @Override
    public Long leftPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 右侧入队（队尾）
     *
     * @param key   键
     * @param value 值
     * @return 入队后列表长度
     */
    @Override
    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 批量右侧入队
     *
     * @param key    键
     * @param values 值集合
     * @return 入队后列表长度
     */
    @Override
    public Long rightPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 按索引设置值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    @Override
    public void set(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    // ==================== 读（安全转换） ====================

    /**
     * 获取指定区间 元素并转换为 List<T>
     *
     * @param key   键
     * @param start 起始索引（含）
     * @param end   结束索引（含），-1 表示末尾
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List；不存在返回空 List
     */
    @Override
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        List<Object> raw = redisTemplate.opsForList().range(key, start, end);
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
     * 获取指定索引元素并转换
     *
     * @param key   键
     * @param index 索引
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的元素；不存在返回 null
     */
    @Override
    public <T> T index(String key, long index, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForList().index(key, index), clazz);
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 长度
     */
    @Override
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ==================== 弹 ====================

    /**
     * 左侧弹出并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 弹出的元素；为空返回 null
     */
    @Override
    public <T> T leftPop(String key, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForList().leftPop(key), clazz);
    }

    /**
     * 右侧弹出并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 弹出的元素；为空返回 null
     */
    @Override
    public <T> T rightPop(String key, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForList().rightPop(key), clazz);
    }

    // ==================== 删 ====================

    /**
     * 删除指定值
     *
     * @param key   键
     * @param count 删除数量：>0 从左往右删 count 个；<0 从右往左删 |count| 个；=0 全删
     * @param value 目标值
     * @return 实际删除数量
     */
    @Override
    public Long remove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 修剪列表，只保留 [start, end] 区间
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     */
    @Override
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }
}
