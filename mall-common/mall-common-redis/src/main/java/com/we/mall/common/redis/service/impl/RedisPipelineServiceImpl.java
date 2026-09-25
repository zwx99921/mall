package com.we.mall.common.redis.service.impl;

import com.we.mall.common.redis.service.RedisPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redis Pipeline 批量操作服务实现
 * <p>
 * 通过 Pipeline 将多条命令打包发送，减少网络往返。
 * <p>
 * 注意事项：
 * 1. Pipeline 不保证原子性，命令之间可能被其他客户端插入
 * 2. 返回结果顺序与命令提交顺序一致
 * 3. 单次 Pipeline 命令数不宜过多（建议 < 1000），避免阻塞 Redis
 * 4. 需要原子性请用 MULTI/EXEC 或 Lua 脚本
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisPipelineServiceImpl implements RedisPipelineService {

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void execute(Consumer<RedisTemplate<String, Object>> consumer) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            consumer.accept(redisTemplate);
            return null;
        });
    }

    @Override
    public void batchSet(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        execute(t -> map.forEach(t.opsForValue()::set));
    }

    @Override
    public void batchDelete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        execute(t -> keys.forEach(t::delete));
    }

    @Override
    public void batchExpire(Collection<String> keys, long timeout, TimeUnit unit) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        execute(t -> keys.forEach(k -> t.expire(k, timeout, unit)));
    }
}
