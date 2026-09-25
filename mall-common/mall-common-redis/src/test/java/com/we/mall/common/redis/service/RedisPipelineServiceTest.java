package com.we.mall.common.redis.service;

import com.we.mall.common.redis.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisPipelineService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis Pipeline 批量操作测试")
public class RedisPipelineServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisPipelineService redisPipelineService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== execute ====================

    @Test
    @DisplayName("execute 自定义命令组合")
    void testExecute() {
        redisPipelineService.execute(t -> {
            t.opsForValue().set("test:pipe:1", "v1");
            t.opsForValue().set("test:pipe:2", "v2");
            t.opsForValue().set("test:pipe:3", "v3");
        });

        assertEquals("v1", redisTemplate.opsForValue().get("test:pipe:1"));
        assertEquals("v2", redisTemplate.opsForValue().get("test:pipe:2"));
        assertEquals("v3", redisTemplate.opsForValue().get("test:pipe:3"));
    }

    @Test
    @DisplayName("execute 混合数据结构")
    void testExecuteMixed() {
        redisPipelineService.execute(t -> {
            t.opsForValue().set("test:pipe:string", "v");
            t.opsForHash().put("test:pipe:hash", "f", "v");
            t.opsForList().rightPush("test:pipe:list", "v");
            t.opsForSet().add("test:pipe:set", "v");
            t.opsForZSet().add("test:pipe:zset", "v", 1.0);
        });

        assertEquals("v", redisTemplate.opsForValue().get("test:pipe:string"));
        assertEquals("v", redisTemplate.opsForHash().get("test:pipe:hash", "f"));
        assertEquals(1L, redisTemplate.opsForList().size("test:pipe:list"));
        assertEquals(1L, redisTemplate.opsForSet().size("test:pipe:set"));
        assertEquals(1L, redisTemplate.opsForZSet().size("test:pipe:zset"));
    }

    @Test
    @DisplayName("execute 空回调返回空结果")
    void testExecuteEmpty() {
        assertDoesNotThrow(() -> redisPipelineService.execute(t -> {
        }));
    }

    // ==================== batchSet ====================

    @Test
    @DisplayName("batchSet 批量写入")
    void testBatchSet() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("test:batch:1", "v1");
        map.put("test:batch:2", "v2");
        map.put("test:batch:3", "v3");

        redisPipelineService.batchSet(map);

        assertEquals("v1", redisTemplate.opsForValue().get("test:batch:1"));
        assertEquals("v2", redisTemplate.opsForValue().get("test:batch:2"));
        assertEquals("v3", redisTemplate.opsForValue().get("test:batch:3"));
    }

    @Test
    @DisplayName("batchSet 空 Map 返回空结果")
    void testBatchSetEmpty() {
        assertDoesNotThrow(() -> redisPipelineService.batchSet(Collections.emptyMap()));
    }

    @Test
    @DisplayName("batchSet null 返回空结果")
    void testBatchSetNull() {
        assertDoesNotThrow(() -> redisPipelineService.batchSet(null));
    }

    // ==================== batchDelete ====================

    @Test
    @DisplayName("batchDelete 批量删除")
    void testBatchDelete() {
        redisTemplate.opsForValue().set("test:batchDel:1", "v1");
        redisTemplate.opsForValue().set("test:batchDel:2", "v2");
        redisTemplate.opsForValue().set("test:batchDel:3", "v3");

        redisPipelineService.batchDelete(Arrays.asList("test:batchDel:1", "test:batchDel:2"));

        assertEquals(Boolean.FALSE, redisTemplate.hasKey("test:batchDel:1"));
        assertEquals(Boolean.FALSE, redisTemplate.hasKey("test:batchDel:2"));
        assertEquals(Boolean.TRUE, redisTemplate.hasKey("test:batchDel:3"));
    }

    @Test
    @DisplayName("batchDelete 空集合返回空结果")
    void testBatchDeleteEmpty() {
        assertDoesNotThrow(() -> redisPipelineService.batchDelete(Collections.emptyList()));
    }

    // ==================== batchExpire ====================

    @Test
    @DisplayName("batchExpire 批量设置过期")
    void testBatchExpire() {
        redisTemplate.opsForValue().set("test:batchExp:1", "v1");
        redisTemplate.opsForValue().set("test:batchExp:2", "v2");

        redisPipelineService.batchExpire(
                Arrays.asList("test:batchExp:1", "test:batchExp:2"),
                60, TimeUnit.SECONDS);

        Long ttl1 = redisTemplate.getExpire("test:batchExp:1", TimeUnit.SECONDS);
        Long ttl2 = redisTemplate.getExpire("test:batchExp:2", TimeUnit.SECONDS);
        assertNotNull(ttl1);
        assertNotNull(ttl2);
        assertTrue(ttl1 > 0 && ttl1 <= 60);
        assertTrue(ttl2 > 0 && ttl2 <= 60);
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：批量写入 1000 条")
    void testBatchLargeScale() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put("test:large:" + i, "v" + i);
        }

        long start = System.currentTimeMillis();
        redisPipelineService.batchSet(map);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals("v0", redisTemplate.opsForValue().get("test:large:0"));
        assertEquals("v999", redisTemplate.opsForValue().get("test:large:999"));

        System.out.println("1000 条批量写入耗时：" + elapsed + "ms");
    }

    @Test
    @DisplayName("场景：批量删除缓存")
    void testBatchClearCache() {
        // 模拟 100 条缓存
        for (int i = 0; i < 100; i++) {
            redisTemplate.opsForValue().set("test:cache:" + i, "v" + i);
        }

        Set<String> keys = redisTemplate.keys("test:cache:*");
        assertNotNull(keys);
        assertEquals(100, keys.size());

        redisPipelineService.batchDelete(keys);

        Set<String> remaining = redisTemplate.keys("test:cache:*");
        assertTrue(remaining.isEmpty());
    }


}
