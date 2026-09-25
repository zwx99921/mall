package com.we.mall.common.redis.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redis Pipeline 批量操作服务接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisPipelineService {

    /**
     * 执行 Pipeline，由调用方通过 consumer 提交命令
     *
     * @param consumer 命令提交回调
     */
    void execute(Consumer<RedisTemplate<String, Object>> consumer);

    /**
     * 批量设置 String 键值对
     *
     * @param map 键值映射
     */
    void batchSet(Map<String, Object> map);

    /**
     * 批量删除 key
     *
     * @param keys 键集合
     */
    void batchDelete(Collection<String> keys);

    /**
     * 批量设置过期时间
     *
     * @param keys    键集合
     * @param timeout 时长
     * @param unit    时间单位
     */
    void batchExpire(Collection<String> keys, long timeout, TimeUnit unit);

}
