package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.convert.RedisTypeConvert;
import com.we.mall.common.redis.service.RedisSetOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Redis Set 类型操作封装
 * <p>
 * 适合做去重、标签、集合运算。读取方法返回类型安全的 Set<T>。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisSetOpsServiceImpl implements RedisSetOpsService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTypeConvert redisTypeConvert;

    /**
     * 添加元素
     *
     * @param key    键
     * @param values 元素可变参数
     * @return 实际新增数量
     */
    @Override
    public Long add(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
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
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 判断元素是否存在
     *
     * @param key   键
     * @param value 元素
     * @return true 存在，false 不存在
     */
    @Override
    public Boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取所有元素并转换为 Set<T>
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 Set；不存在返回空 Set
     */
    @Override
    public <T> Set<T> members(String key, Class<T> clazz) {
        Set<Object> raw = redisTemplate.opsForSet().members(key);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }

    /**
     * 获取集合大小
     *
     * @param key 键
     * @return 元素数量
     */
    @Override
    public Long size(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 随机取一个元素并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 随机元素；为空返回 null
     */
    @Override
    public <T> T randomMember(String key, Class<T> clazz) {
        return redisTypeConvert.convert(redisTemplate.opsForSet().randomMember(key), clazz);
    }

    /**
     * 求交集并转换
     *
     * @param key      键
     * @param otherKey 另一键
     * @param clazz    元素类型
     * @param <T>      元素泛型
     * @return 交集结果
     */
    @Override
    public <T> Set<T> intersect(String key, String otherKey, Class<T> clazz) {
        Set<Object> raw = redisTemplate.opsForSet().intersect(key, otherKey);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }

    /**
     * 求并集并转换
     *
     * @param key      键
     * @param otherKey 另一键
     * @param clazz    元素类型
     * @param <T>      元素泛型
     * @return 并集结果
     */
    @Override
    public <T> Set<T> union(String key, String otherKey, Class<T> clazz) {
        Set<Object> raw = redisTemplate.opsForSet().union(key, otherKey);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object o : raw) {
            result.add(redisTypeConvert.convert(o, clazz));
        }
        return result;
    }
}
