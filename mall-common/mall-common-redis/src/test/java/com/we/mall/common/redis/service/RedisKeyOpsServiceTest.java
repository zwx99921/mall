package com.we.mall.common.redis.service;

import com.we.mall.common.redis.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisKeyOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis Key 操作测试")
public class RedisKeyOpsServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisKeyOpsService redisKeyOpsService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete 删除单个 key")
    void testDeleteSingle() {
        redisTemplate.opsForValue().set("test:key:1", "v1");

        Boolean result = redisKeyOpsService.delete("test:key:1");

        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.FALSE, redisKeyOpsService.hasKey("test:key:1"));
    }

    @Test
    @DisplayName("delete 删除不存在的 key 返回 false")
    void testDeleteNotExists() {
        Boolean result = redisKeyOpsService.delete("test:key:not:exists");
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    @DisplayName("delete 批量删除")
    void testDeleteBatch() {
        redisTemplate.opsForValue().set("test:key:1", "v1");
        redisTemplate.opsForValue().set("test:key:2", "v2");
        redisTemplate.opsForValue().set("test:key:3", "v3");

        Long deleted = redisKeyOpsService.delete(Arrays.asList("test:key:1", "test:key:2"));

        assertEquals(2L, deleted);
        assertEquals(Boolean.TRUE, redisKeyOpsService.hasKey("test:key:3"));
    }

    // ==================== hasKey ====================

    @Test
    @DisplayName("hasKey 存在 / 不存在")
    void testHasKey() {
        redisTemplate.opsForValue().set("test:key:exists", "v");

        assertEquals(Boolean.TRUE, redisKeyOpsService.hasKey("test:key:exists"));
        assertEquals(Boolean.FALSE, redisKeyOpsService.hasKey("test:key:not:exists"));
    }

    // ==================== expire / getExpire ====================

    @Test
    @DisplayName("expire 设置过期时间")
    void testExpire() {
        redisTemplate.opsForValue().set("test:key:ttl", "v");

        Boolean result = redisKeyOpsService.expire("test:key:ttl", 60, TimeUnit.SECONDS);

        assertEquals(Boolean.TRUE, result);
        Long ttl = redisKeyOpsService.getExpire("test:key:ttl", TimeUnit.SECONDS);
        assertNotNull(ttl);
        assertTrue(ttl > 0 && ttl <= 60);
    }

    @Test
    @DisplayName("getExpire -1 表示永久")
    void testGetExpirePersistent() {
        redisTemplate.opsForValue().set("test:key:no:ttl", "v");

        Long ttl = redisKeyOpsService.getExpire("test:key:no:ttl", TimeUnit.SECONDS);

        assertEquals(-1L, ttl);
    }

    @Test
    @DisplayName("getExpire -2 表示 key 不存在")
    void testGetExpireNotExists() {
        Long ttl = redisKeyOpsService.getExpire("test:key:not:exists", TimeUnit.SECONDS);

        assertEquals(-2L, ttl);
    }

    // ==================== persist ====================

    @Test
    @DisplayName("persist 移除过期时间")
    void testPersist() {
        redisTemplate.opsForValue().set("test:key:persist", "v", 60, TimeUnit.SECONDS);

        Boolean result = redisKeyOpsService.persist("test:key:persist");

        assertEquals(Boolean.TRUE, result);
        assertEquals(-1L, redisKeyOpsService.getExpire("test:key:persist", TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("persist 无过期时间的 key 返回 false")
    void testPersistNoTtl() {
        redisTemplate.opsForValue().set("test:key:no:ttl2", "v");

        Boolean result = redisKeyOpsService.persist("test:key:no:ttl2");

        assertEquals(Boolean.FALSE, result);
    }

    // ==================== type ====================

    @Test
    @DisplayName("type string")
    void testTypeString() {
        redisTemplate.opsForValue().set("test:key:string", "v");
        assertEquals("string", redisKeyOpsService.type("test:key:string"));
    }

    @Test
    @DisplayName("type hash")
    void testTypeHash() {
        redisTemplate.opsForHash().put("test:key:hash", "f", "v");
        assertEquals("hash", redisKeyOpsService.type("test:key:hash"));
    }

    @Test
    @DisplayName("type list")
    void testTypeList() {
        redisTemplate.opsForList().rightPush("test:key:list", "v");
        assertEquals("list", redisKeyOpsService.type("test:key:list"));
    }

    @Test
    @DisplayName("type set")
    void testTypeSet() {
        redisTemplate.opsForSet().add("test:key:set", "v");
        assertEquals("set", redisKeyOpsService.type("test:key:set"));
    }

    @Test
    @DisplayName("type zset")
    void testTypeZSet() {
        redisTemplate.opsForZSet().add("test:key:zset", "v", 1.0);
        assertEquals("zset", redisKeyOpsService.type("test:key:zset"));
    }

    @Test
    @DisplayName("type 不存在的 key 返回 null")
    void testTypeNotExists() {
        assertNull(redisKeyOpsService.type("test:key:not:exists"));
    }

    // ==================== scan ====================

    @Test
    @DisplayName("scan 匹配 pattern")
    void testScan() {
        redisTemplate.opsForValue().set("test:scan:user:1", "u1");
        redisTemplate.opsForValue().set("test:scan:user:2", "u2");
        redisTemplate.opsForValue().set("test:scan:order:1", "o1");

        Set<String> result = redisKeyOpsService.scan("test:scan:user:*", 100);

        assertEquals(2, result.size());
        assertTrue(result.contains("test:scan:user:1"));
        assertTrue(result.contains("test:scan:user:2"));
    }

    @Test
    @DisplayName("scan 无匹配返回空 Set")
    void testScanEmpty() {
        Set<String> result = redisKeyOpsService.scan("test:scan:not:exists:*", 100);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("scan 匹配所有")
    void testScanAll() {
        redisTemplate.opsForValue().set("test:scan:all:1", "v");
        redisTemplate.opsForValue().set("test:scan:all:2", "v");

        Set<String> result = redisKeyOpsService.scan("test:scan:all:*", 100);

        assertEquals(2, result.size());
    }

    // ==================== rename ====================

    @Test
    @DisplayName("rename 重命名 key")
    void testRename() {
        redisTemplate.opsForValue().set("test:key:old", "v");

        redisKeyOpsService.rename("test:key:old", "test:key:new");

        assertEquals(Boolean.FALSE, redisKeyOpsService.hasKey("test:key:old"));
        assertEquals(Boolean.TRUE, redisKeyOpsService.hasKey("test:key:new"));
        assertEquals("v", redisTemplate.opsForValue().get("test:key:new"));
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：按前缀批量清理缓存")
    void testClearByPrefix() {
        redisTemplate.opsForValue().set("test:cache:user:1", "u1");
        redisTemplate.opsForValue().set("test:cache:user:2", "u2");
        redisTemplate.opsForValue().set("test:cache:order:1", "o1");

        Set<String> userKeys = redisKeyOpsService.scan("test:cache:user:*", 100);
        Long deleted = redisKeyOpsService.delete(userKeys);

        assertEquals(2L, deleted);
        assertEquals(Boolean.TRUE, redisKeyOpsService.hasKey("test:cache:order:1"));
    }

}
