package com.we.mall.common.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.result.R;
import com.we.mall.common.redis.TestApplication;
import com.we.mall.common.redis.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisCacheService 单元测试
 * <p>
 * 覆盖三防：
 * 1. 防穿透：空值缓存
 * 2. 防击穿：互斥锁重建
 * 3. 防雪崩：随机 TTL
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis 缓存读取测试")
public class RedisCacheServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisCacheService redisCacheService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== 防穿透 ====================

    @Test
    @DisplayName("getOrLoad 首次未命中，回源并写缓存")
    void testGetOrLoadFirstMiss() {
        String key = "test:cache:user:1";
        AtomicInteger loadCount = new AtomicInteger(0);

        User result = redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(1L, "张三");
        }, 600, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张三", result.getName());
        assertEquals(1, loadCount.get());
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    @Test
    @DisplayName("getOrLoad 二次命中，不回源")
    void testGetOrLoadSecondHit() {
        String key = "test:cache:user:2";
        AtomicInteger loadCount = new AtomicInteger(0);

        redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(2L, "李四");
        }, 600, TimeUnit.SECONDS);

        User result = redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(2L, "李四");
        }, 600, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(1, loadCount.get(), "第二次应命中缓存，不回源");
    }

    @Test
    @DisplayName("getOrLoad loader 返回 null，写空值标记")
    void testGetOrLoadNullValue() {
        String key = "test:cache:user:null";
        AtomicInteger loadCount = new AtomicInteger(0);

        User result = redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return null;
        }, 600, TimeUnit.SECONDS);

        assertNull(result);
        assertEquals(1, loadCount.get());
        // 空值标记已写入
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    @Test
    @DisplayName("getOrLoad 空值缓存生效，二次不回源")
    void testGetOrLoadNullCacheHit() {
        String key = "test:cache:user:null:hit";
        AtomicInteger loadCount = new AtomicInteger(0);

        // 第一次：回源返回 null
        redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return null;
        }, 600, TimeUnit.SECONDS);

        // 第二次：命中空值标记，不回源
        User result = redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return null;
        }, 600, TimeUnit.SECONDS);

        assertNull(result);
        assertEquals(1, loadCount.get(), "空值缓存生效，第二次不回源");
    }

    // ==================== 泛型对象 ====================

    @Test
    @DisplayName("getOrLoad 泛型对象 Result<User>")
    void testGetOrLoadTypeReference() {
        String key = "test:cache:result:1";

        R<User> result = redisCacheService.getOrLoad(
                key,
                new TypeReference<R<User>>() {
                },
                () -> R.ok(new User(1L, "张三")),
                600,
                TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertEquals(ResultCode.SUCCESS.getMessage(), result.getMessage());
        assertNotNull(result.getData());
        assertEquals("张三", result.getData().getName());
    }

    // ==================== List ====================

    @Test
    @DisplayName("getOrLoadList 正常返回")
    void testGetOrLoadList() {
        String key = "test:cache:user:list";

        List<User> result = redisCacheService.getOrLoadList(key, User.class,
                () -> Arrays.asList(new User(1L, "张三"), new User(2L, "李四")),
                600, TimeUnit.SECONDS);

        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
    }

    @Test
    @DisplayName("getOrLoadList loader 返回 null，返回空 List")
    void testGetOrLoadListNull() {
        String key = "test:cache:user:list:null";

        List<User> result = redisCacheService.getOrLoadList(key, User.class,
                () -> null, 600, TimeUnit.SECONDS);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getOrLoadList loader 返回空 List，写空值标记")
    void testGetOrLoadListEmpty() {
        String key = "test:cache:user:list:empty";
        AtomicInteger loadCount = new AtomicInteger(0);

        redisCacheService.getOrLoadList(key, User.class, () -> {
            loadCount.incrementAndGet();
            return Collections.emptyList();
        }, 600, TimeUnit.SECONDS);

        List<User> result = redisCacheService.getOrLoadList(key, User.class, () -> {
            loadCount.incrementAndGet();
            return Collections.emptyList();
        }, 600, TimeUnit.SECONDS);

        assertTrue(result.isEmpty());
        assertEquals(1, loadCount.get(), "空 List 缓存生效");
    }

    // ==================== 防击穿 ====================

    @Test
    @DisplayName("getOrLoadWithLock 首次未命中，回源并写缓存")
    void testGetOrLoadWithLockFirstMiss() {
        String key = "test:cache:lock:user:1";
        AtomicInteger loadCount = new AtomicInteger(0);

        User result = redisCacheService.getOrLoadWithLock(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(1L, "张三");
        }, 600, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(1, loadCount.get());
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    @Test
    @DisplayName("getOrLoadWithLock 二次命中，不回源")
    void testGetOrLoadWithLockSecondHit() {
        String key = "test:cache:lock:user:2";
        AtomicInteger loadCount = new AtomicInteger(0);

        redisCacheService.getOrLoadWithLock(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(2L, "李四");
        }, 600, TimeUnit.SECONDS);

        redisCacheService.getOrLoadWithLock(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(2L, "李四");
        }, 600, TimeUnit.SECONDS);

        assertEquals(1, loadCount.get());
    }

    @Test
    @DisplayName("getOrLoadWithLock 并发下只回源一次（防击穿）")
    void testGetOrLoadWithLockConcurrent() throws Exception {
        String key = "test:cache:lock:concurrent";
        int threadCount = 20;
        AtomicInteger loadCount = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    redisCacheService.getOrLoadWithLock(key, User.class, () -> {
                        loadCount.incrementAndGet();
                        // 模拟回源耗时
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                        return new User(1L, "张三");
                    }, 600, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(1, loadCount.get(), "并发下只应回源一次");
    }

    @Test
    @DisplayName("getOrLoadWithLock 泛型对象")
    void testGetOrLoadWithLockTypeReference() {
        String key = "test:cache:lock:result";

        R<User> result = redisCacheService.getOrLoadWithLock(key,
                new TypeReference<R<User>>() {
                },
                () -> R.ok(new User(1L, "张三")),
                600, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertNotNull(result.getData());
        assertEquals("张三", result.getData().getName());
    }

    // ==================== 防雪崩 ====================

    @Test
    @DisplayName("getOrLoadWithRandomTtl TTL 在 [base, base+range)")
    void testGetOrLoadWithRandomTtl() {
        String key = "test:cache:random:ttl";

        User result = redisCacheService.getOrLoadWithRandomTtl(key, User.class,
                () -> new User(1L, "张三"),
                60, 30);

        assertNotNull(result);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertNotNull(ttl);
        assertTrue(ttl >= 60 && ttl < 90, "TTL 应在 [60, 90)，实际：" + ttl);
    }

    @Test
    @DisplayName("getOrLoadWithRandomTtl loader 返回 null，写空值标记")
    void testGetOrLoadWithRandomTtlNull() {
        String key = "test:cache:random:null";

        User result = redisCacheService.getOrLoadWithRandomTtl(key, User.class,
                () -> null, 60, 30);

        assertNull(result);
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    // ==================== put / evict ====================

    @Test
    @DisplayName("put 写入缓存")
    void testPut() {
        String key = "test:cache:put";
        redisCacheService.put(key, new User(1L, "张三"), 600, TimeUnit.SECONDS);

        assertEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    @Test
    @DisplayName("evict 删除缓存")
    void testEvict() {
        String key = "test:cache:evict";
        redisCacheService.put(key, new User(1L, "张三"), 600, TimeUnit.SECONDS);

        Boolean result = redisCacheService.evict(key);

        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.FALSE, redisTemplate.hasKey(key));
    }

    @Test
    @DisplayName("evict 不存在的 key 返回 false")
    void testEvictNotExists() {
        Boolean result = redisCacheService.evict("test:cache:evict:not:exists");
        assertEquals(Boolean.FALSE, result);
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：缓存过期后回源")
    void testCacheExpireAndReload() throws InterruptedException {
        String key = "test:cache:expire";
        AtomicInteger loadCount = new AtomicInteger(0);

        redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(1L, "张三");
        }, 1, TimeUnit.SECONDS);

        Thread.sleep(1100);

        redisCacheService.getOrLoad(key, User.class, () -> {
            loadCount.incrementAndGet();
            return new User(1L, "张三");
        }, 1, TimeUnit.SECONDS);

        assertEquals(2, loadCount.get(), "缓存过期后应重新回源");
    }

}
