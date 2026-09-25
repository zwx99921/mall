package com.we.mall.common.redis.service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用 Key 操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisKeyOpsService {

    /**
     * 删除单个 key
     *
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 批量删除 key
     *
     * @param keys 键集合
     * @return 实际删除的 key 数量
     */
    Long delete(Collection<String> keys);

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    Boolean hasKey(String key);

    /**
     * 为 key 设置过期时间
     *
     * @param key     键
     * @param timeout 时长
     * @param unit    时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 移除 key 的过期时间，使其永久有效
     *
     * @param key 键
     * @return 是否移除成功
     */
    Boolean persist(String key);

    /**
     * 获取 key 的剩余过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return 剩余时间；-1 表示永久，-2 表示 key 不存在
     */
    Long getExpire(String key, TimeUnit unit);

    /**
     * 获取 key 对应的数据类型
     *
     * @param key 键
     * @return 类型编码：string / hash / list / set / zset；key 不存在返回 null
     */
    String type(String key);

    /**
     * 使用 SCAN 游标遍历匹配的 key，替代 keys（避免阻塞 Redis）
     *
     * @param pattern 匹配模式，如 "app:user:*"
     * @param count   每次扫描的建议数量
     * @return 匹配的 key 集合
     */
    Set<String> scan(String pattern, long count);

    /**
     * 重命名 key
     *
     * @param oldKey 旧键
     * @param newKey 新键
     */
    void rename(String oldKey, String newKey);

}
