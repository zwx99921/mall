package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.service.RedisKeyOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用 Key 操作封装实现
 * <p>
 * 提供 key 维度的操作：删除、判断存在、设置过期、扫描、重命名等。
 * 不涉及具体数据结构，所有数据类型均可用。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisKeyOpsServiceImpl implements RedisKeyOpsService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 删除单个 key
     *
     * @param key 键
     * @return 是否删除成功
     */
    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除 key
     *
     * @param keys 键集合
     * @return 实际删除的 key 数量
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 为 key 设置过期时间
     *
     * @param key     键
     * @param timeout 时长
     * @param unit    时间单位
     * @return 是否设置成功
     */
    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 移除 key 的过期时间，使其永久有效
     *
     * @param key 键
     * @return 是否移除成功
     */
    @Override
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 获取 key 的剩余过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return 剩余时间；-1 表示永久，-2 表示 key 不存在
     */
    @Override
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 获取 key 对应的数据类型
     *
     * @param key 键
     * @return 类型编码：string / hash / list / set / zset；key 不存在返回 null
     */
    @Override
    public String type(String key) {
        DataType type = redisTemplate.type(key);
        return DataType.NONE.equals(type) ? null : type.code();
    }

    /**
     * 使用 SCAN 游标遍历匹配的 key，替代 keys（避免阻塞 Redis）
     *
     * @param pattern 匹配模式，如 "app:user:*"
     * @param count   每次扫描的建议数量
     * @return 匹配的 key 集合
     */
    @Override
    public Set<String> scan(String pattern, long count) {
        Set<String> result = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    result.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return null;
        });
        return result;
    }

    /**
     * 重命名 key
     *
     * @param oldKey 旧键
     * @param newKey 新键
     */
    @Override
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }
}
