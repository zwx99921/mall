package com.we.mall.common.redis.service.impl;

import com.we.mall.common.core.exception.SystemException;
import com.we.mall.common.redis.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁封装
 * <p>
 * 基于 Redisson 的 RLock，支持可重入、自动续期（看门狗）、公平锁。
 * <p>
 * 使用建议：
 * 1. 加锁必须指定 leaseTime，避免业务异常导致锁不释放
 * 2. 需要自动续期时，leaseTime 传 -1，由看门狗续期（默认 30s 续一次）
 * 3. 加锁失败可直接抛异常或返回 false，视业务而定
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLockServiceImpl implements RedisLockService {


    /**
     * executeWithLock 默认等待时长（秒）
     */
    private static final long DEFAULT_WAIT_SECONDS = 3L;

    private final RedissonClient redissonClient;

    /**
     * 尝试加锁（不等待，立即返回）
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @return true 加锁成功，false 加锁失败
     */
    @Override
    public boolean tryLock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(0, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("tryLock interrupted, key={}", lockKey, e);
            return false;
        }
    }

    /**
     * 尝试加锁（带等待时间）
     *
     * @param lockKey   锁 key
     * @param waitTime  最大等待时长
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @return true 加锁成功，false 加锁失败
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("tryLock interrupted, key={}", lockKey, e);
            return false;
        }
    }

    /**
     * 加锁并执行，自动释放
     * <p>
     * 加锁失败直接抛异常，适合「必须成功」的场景。
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长；-1 表示启用看门狗自动续期
     * @param unit      时间单位
     * @param action    业务逻辑
     * @param <T>       返回值类型
     * @return 业务返回值
     * @throws IllegalStateException 加锁失败
     */
    @Override
    public <T> T executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Supplier<T> action) {
//        RLock lock = redissonClient.getLock(lockKey);
//        boolean locked = false;
//        try {
//            locked = lock.tryLock(0, leaseTime, unit);
//            if (!locked) {
//                throw new IllegalStateException("获取分布式锁失败: " + lockKey);
//            }
//            return action.get();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new IllegalStateException("获取分布式锁被中断: " + lockKey, e);
//        } finally {
//            if (locked && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
        return executeWithLock(lockKey, DEFAULT_WAIT_SECONDS, leaseTime, unit, action);
    }

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
     * @throws SystemException 加锁失败
     */
    @Override
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, unit);
            if (!locked) {
                throw SystemException.of("获取分布式锁失败: " + lockKey);
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SystemException.of("获取分布式锁被中断: " + lockKey, e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 加锁并执行（无返回值）
     *
     * @param lockKey   锁 key
     * @param leaseTime 锁自动释放时长
     * @param unit      时间单位
     * @param action    业务逻辑
     */
    @Override
    public void executeWithLock(String lockKey, long leaseTime, TimeUnit unit, Runnable action) {
        executeWithLock(lockKey, leaseTime, unit, () -> {
            action.run();
            return null;
        });
    }

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
    @Override
    public RLock lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean ok = lock.tryLock(0, leaseTime, unit);
            return ok ? lock : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 手动解锁
     *
     * @param lock RLock，可为 null
     */
    @Override
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 获取公平锁（按请求顺序获取）
     *
     * @param lockKey 锁 key
     * @return 公平锁 RLock
     */
    @Override
    public RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    /**
     * 获取读写锁
     *
     * @param lockKey 锁 key
     * @return 读写锁
     */
    @Override
    public RReadWriteLock getReadWriteLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey);
    }
}
