package com.we.mall.common.redis.service;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisLockService {

    /**
     * 尝试加锁（不等待，立即返回）
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @return true 加锁成功，false 加锁失败
     */
    boolean tryLock(String lockKey, long leaseTime, TimeUnit unit);

    /**
     * 尝试加锁（带等待时间）
     *
     * @param lockKey   锁 key
     * @param waitTime  最大等待时长
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @return true 加锁成功，false 加锁失败
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 加锁并执行，自动释放
     * <p>
     * 默认等待 3 秒，拿不到锁抛 IllegalStateException。
     * 适合竞争不激烈的场景。
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @param action    业务逻辑
     * @param <T>       返回值类型
     * @return 业务返回值
     * @throws IllegalStateException 加锁失败
     */
    <T> T executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Supplier<T> action);

    /**
     * 加锁并执行（带等待时间），自动释放
     *
     * @param lockKey   锁 key
     * @param waitTime  最大等待时长
     * @param leaseTime 锁自动释放时长
     * @param unit      时间单位
     * @param action    业务逻辑
     * @param <T>       返回值类型
     * @return 业务返回值
     * @throws IllegalStateException 加锁失败
     */
    <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> action);

    /**
     * 加锁并执行（无返回值）
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长
     * @param unit      时间单位
     * @param action    业务逻辑
     */
    void executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Runnable action);

    /**
     * 手动加锁，返回 RLock，由调用方自行 unlock
     * <p>
     * 仅在你需要跨方法持有锁时使用，务必在 finally 中 unlock。
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长
     * @param unit      时间单位
     * @return RLock；加锁失败返回 null
     */
    RLock lock(String lockKey, long leaseTime, TimeUnit unit);

    /**
     * 手动解锁
     *
     * @param lock RLock，可为 null
     */
    void unlock(RLock lock);

    /**
     * 获取公平锁（按请求顺序获取）
     *
     * @param lockKey 锁 key
     * @return 公平锁 RLock
     */
    RLock getFairLock(String lockKey);

    /**
     * 获取读写锁
     *
     * @param lockKey 锁 key
     * @return 读写锁
     */
    RReadWriteLock getReadWriteLock(String lockKey);

}
