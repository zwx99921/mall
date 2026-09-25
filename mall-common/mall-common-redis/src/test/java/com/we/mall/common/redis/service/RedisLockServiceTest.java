package com.we.mall.common.redis.service;

import com.we.mall.common.redis.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisLockService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redisson 分布式锁测试")
public class RedisLockServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisLockService redisLockService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== tryLock ====================

    @Test
    @DisplayName("tryLock 加锁成功")
    void testTryLockSuccess() {
        String key = "test:lock:1";
        assertTrue(redisLockService.tryLock(key, 10, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("tryLock 不同线程加锁失败")
    void testTryLockFailByOtherThread() throws Exception {
        String key = "test:lock:2";
        redisLockService.tryLock(key, 10, TimeUnit.SECONDS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() ->
                redisLockService.tryLock(key, 10, TimeUnit.SECONDS));
        Boolean result = future.get(5, TimeUnit.SECONDS);

        assertFalse(result, "其他线程加锁应失败");
        executor.shutdown();
    }

    @Test
    @DisplayName("tryLock 带等待时间")
    void testTryLockWithWait() throws Exception {
        String key = "test:lock:wait";

        // 线程1 先加锁，2 秒后释放
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch released = new CountDownLatch(1);

        // 线程1：加锁 → 1 秒后释放
        executor.submit(() -> {
            RLock lock = redisLockService.lock(key, 10, TimeUnit.SECONDS);
            try {
                locked.countDown();
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            } finally {
                redisLockService.unlock(lock);
                released.countDown();
            }
        });

        locked.await();

        // 线程2：等待 3 秒
        boolean acquired = redisLockService.tryLock(key, 3, 10, TimeUnit.SECONDS);
        assertTrue(acquired, "线程2 应在线程1 释放后获取锁");

        released.await();
        executor.shutdown();
    }

    // ==================== executeWithLock ====================

    @Test
    @DisplayName("executeWithLock 加锁执行并自动释放")
    void testExecuteWithLock() {
        String key = "test:lock:exec";

        String result = redisLockService.executeWithLock(key, 10, TimeUnit.SECONDS, () -> "ok");

        assertEquals("ok", result);

        // 锁已释放，可以再次获取
        boolean canLock = redisLockService.tryLock(key, 10, TimeUnit.SECONDS);
        assertTrue(canLock, "锁应已释放");
    }

    @Test
    @DisplayName("executeWithLock 业务异常时也释放锁")
    void testExecuteWithLockException() {
        String key = "test:lock:exec:exception";

        assertThrows(RuntimeException.class, () ->
                redisLockService.executeWithLock(key, 10, TimeUnit.SECONDS, () -> {
                    throw new RuntimeException("业务异常");
                }));

        // 锁已释放
        assertTrue(redisLockService.tryLock(key, 10, TimeUnit.SECONDS), "异常后锁应已释放");
    }

    @Test
    @DisplayName("executeWithLock 无返回值版本")
    void testExecuteWithLockRunnable() {
        String key = "test:lock:exec:runnable";
        AtomicInteger counter = new AtomicInteger(0);

        redisLockService.executeWithLock(key, 10, TimeUnit.SECONDS, counter::incrementAndGet);

        assertEquals(1, counter.get());

        // 锁已释放
        assertTrue(redisLockService.tryLock(key, 10, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("executeWithLock 带等待时间")
    void testExecuteWithLockWithWait() {
        String key = "test:lock:exec:wait";

        String result = redisLockService.executeWithLock(key, 3, 10, TimeUnit.SECONDS, () -> "ok");

        assertEquals("ok", result);
    }

    // ==================== 并发互斥 ====================

    @Test
    @DisplayName("并发下互斥：20 线程累加，结果正确")
    void testConcurrentMutex() throws Exception {
        String key = "test:lock:concurrent";
        int threadCount = 20;
        int loop = 100;
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < loop; j++) {
                        // 用带等待时间的重载，避免直接失败
                        redisLockService.executeWithLock(key, 10, 10, TimeUnit.SECONDS,
                                counter::incrementAndGet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * loop, counter.get(), "并发互斥下累加结果应正确");
    }

    // ==================== 手动加锁 ====================

    @Test
    @DisplayName("lock + unlock 手动加解锁")
    void testManualLock() {
        String key = "test:lock:manual";

        RLock lock = redisLockService.lock(key, 10, TimeUnit.SECONDS);
        assertNotNull(lock);
        assertTrue(lock.isHeldByCurrentThread());

        redisLockService.unlock(lock);

        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    @DisplayName("unlock null 不报错")
    void testUnlockNull() {
        assertDoesNotThrow(() -> redisLockService.unlock(null));
    }

    // ==================== 公平锁 ====================

    @Test
    @DisplayName("getFairLock 返回公平锁")
    void testGetFairLock() throws InterruptedException {
        String key = "test:lock:fair";

        RLock fairLock = redisLockService.getFairLock(key);
        assertNotNull(fairLock);

        assertTrue(fairLock.tryLock(0, 10, TimeUnit.SECONDS));

        if (fairLock.isHeldByCurrentThread()) {
            fairLock.unlock();
        }
    }

    // ==================== 读写锁 ====================

    @Test
    @DisplayName("getReadWriteLock 读锁可并发")
    void testReadWriteLockRead() throws InterruptedException {
        String key = "test:lock:rw:read";

        RReadWriteLock rwLock = redisLockService.getReadWriteLock(key);
        assertNotNull(rwLock);

        RLock readLock1 = rwLock.readLock();
        RLock readLock2 = rwLock.readLock();

        assertTrue(readLock1.tryLock(0, 10, TimeUnit.SECONDS));
        assertTrue(readLock2.tryLock(0, 10, TimeUnit.SECONDS), "读锁可并发获取");

        if (readLock1.isHeldByCurrentThread()) readLock1.unlock();
        if (readLock2.isHeldByCurrentThread()) readLock2.unlock();
    }

    @Test
    @DisplayName("写锁互斥（不同线程）")
    void testWriteLockMutex() throws Exception {
        String key = "test:lock:rw:write";
        RReadWriteLock rwLock = redisLockService.getReadWriteLock(key);

        RLock writeLock1 = rwLock.writeLock();
        writeLock1.tryLock(0, 10, TimeUnit.SECONDS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            RLock writeLock2 = rwLock.writeLock();
            return writeLock2.tryLock(0, 10, TimeUnit.SECONDS);
        });
        Boolean result = future.get(5, TimeUnit.SECONDS);

        assertFalse(result, "其他线程写锁应失败");

        if (writeLock1.isHeldByCurrentThread()) writeLock1.unlock();
        executor.shutdown();
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：扣减库存（防超卖）")
    void testDeductStock() throws Exception {
        String stockKey = "test:stock:sku1";
        redisTemplate.opsForValue().set(stockKey, 100);

        String lockKey = "test:lock:stock:sku1";
        int threadCount = 20;
        int perThread = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < perThread; j++) {
                        // 用带等待时间的重载，避免直接失败
                        redisLockService.executeWithLock(lockKey, 10, 10, TimeUnit.SECONDS, () -> {
                            Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);
                            if (stock != null && stock > 0) {
                                redisTemplate.opsForValue().set(stockKey, stock - 1);
                            }
                            return null;
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        Integer finalStock = (Integer) redisTemplate.opsForValue().get(stockKey);
        assertEquals(0, finalStock, "库存应扣减到 0，不超卖");
    }


}
