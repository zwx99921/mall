package com.we.mall.common.redis.service;

import com.we.mall.common.redis.TestApplication;
import com.we.mall.common.redis.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisHashOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis Hash 操作测试")
public class RedisHashOpsServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisHashOpsService redisHashOpsService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== put / get ====================

    @Test
    @DisplayName("put + get 单字段")
    void testPutAndGet() {
        String key = "test:hash:user";
        redisHashOpsService.put(key, "name", "张三");

        assertEquals("张三", redisHashOpsService.get(key, "name", String.class));
    }

    @Test
    @DisplayName("put + get 对象字段")
    void testPutAndGetObject() {
        String key = "test:hash:user:obj";
        User user = new User(1L, "张三", 20);

        redisHashOpsService.put(key, "user", user);
        User result = redisHashOpsService.get(key, "user", User.class);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("get 不存在的 field 返回 null")
    void testGetNotExists() {
        assertNull(redisHashOpsService.get("test:hash:not:exists", "name", String.class));
    }

    // ==================== putAll / getAll ====================

    @Test
    @DisplayName("putAll + getAll")
    void testPutAllAndGetAll() {
        String key = "test:hash:config";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", "20");
        map.put("city", "北京");

        redisHashOpsService.putAll(key, map);
        Map<String, String> result = redisHashOpsService.getAll(key, String.class);

        assertEquals(3, result.size());
        assertEquals("张三", result.get("name"));
        assertEquals("20", result.get("age"));
        assertEquals("北京", result.get("city"));
    }

    @Test
    @DisplayName("getAll 不存在的 key 返回空 Map")
    void testGetAllNotExists() {
        Map<String, String> result = redisHashOpsService.getAll("test:hash:not:exists", String.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== putIfAbsent ====================

    @Test
    @DisplayName("putIfAbsent 首次成功，再次失败")
    void testPutIfAbsent() {
        String key = "test:hash:absent";
        Boolean first = redisHashOpsService.putIfAbsent(key, "name", "张三");
        Boolean second = redisHashOpsService.putIfAbsent(key, "name", "李四");

        assertEquals(Boolean.TRUE, first);
        assertEquals(Boolean.FALSE, second);
        assertEquals("张三", redisHashOpsService.get(key, "name", String.class));
    }

    // ==================== multiGet ====================

    @Test
    @DisplayName("multiGet 批量读取，顺序一致")
    void testMultiGet() {
        String key = "test:hash:multi";
        redisHashOpsService.put(key, "f1", "v1");
        redisHashOpsService.put(key, "f2", "v2");
        redisHashOpsService.put(key, "f3", "v3");

        List<String> fields = Arrays.asList("f1", "f2", "f3");
        List<String> result = redisHashOpsService.multiGet(key, fields, String.class);

        assertEquals(3, result.size());
        assertEquals("v1", result.get(0));
        assertEquals("v2", result.get(1));
        assertEquals("v3", result.get(2));
    }

    @Test
    @DisplayName("multiGet 空字段集合返回空 List")
    void testMultiGetEmpty() {
        List<String> result = redisHashOpsService.multiGet("test:hash:multi", Collections.emptyList(), String.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== keys ====================

    @Test
    @DisplayName("keys 获取所有 field 名")
    void testKeys() {
        String key = "test:hash:keys";
        redisHashOpsService.put(key, "name", "张三");
        redisHashOpsService.put(key, "age", "20");

        Set<String> result = redisHashOpsService.keys(key);

        assertEquals(2, result.size());
        assertTrue(result.contains("name"));
        assertTrue(result.contains("age"));
    }

    @Test
    @DisplayName("keys 不存在的 key 返回空 Set")
    void testKeysNotExists() {
        Set<String> result = redisHashOpsService.keys("test:hash:not:exists");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== size / hasKey ====================

    @Test
    @DisplayName("size 获取 field 数量")
    void testSize() {
        String key = "test:hash:size";
        redisHashOpsService.put(key, "f1", "v1");
        redisHashOpsService.put(key, "f2", "v2");

        assertEquals(2L, redisHashOpsService.size(key));
    }

    @Test
    @DisplayName("hasKey 判断 field 是否存在")
    void testHasKey() {
        String key = "test:hash:has";
        redisHashOpsService.put(key, "name", "张三");

        assertEquals(Boolean.TRUE, redisHashOpsService.hasKey(key, "name"));
        assertEquals(Boolean.FALSE, redisHashOpsService.hasKey(key, "notExist"));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete 删除单个 field")
    void testDeleteSingle() {
        String key = "test:hash:delete";
        redisHashOpsService.put(key, "f1", "v1");
        redisHashOpsService.put(key, "f2", "v2");

        Long deleted = redisHashOpsService.delete(key, "f1");

        assertEquals(1L, deleted);
        assertEquals(Boolean.FALSE, redisHashOpsService.hasKey(key, "f1"));
        assertEquals(Boolean.TRUE, redisHashOpsService.hasKey(key, "f2"));
    }

    @Test
    @DisplayName("delete 删除多个 field")
    void testDeleteMultiple() {
        String key = "test:hash:delete:multi";
        redisHashOpsService.put(key, "f1", "v1");
        redisHashOpsService.put(key, "f2", "v2");
        redisHashOpsService.put(key, "f3", "v3");

        Long deleted = redisHashOpsService.delete(key, "f1", "f2");

        assertEquals(2L, deleted);
        assertEquals(1L, redisHashOpsService.size(key));
    }

    // ==================== increment ====================

    @Test
    @DisplayName("increment 长整型自增")
    void testIncrementLong() {
        String key = "test:hash:incr:long";
        redisHashOpsService.put(key, "count", 100L);

        assertEquals(101L, redisHashOpsService.increment(key, "count", 1L));
        assertEquals(111L, redisHashOpsService.increment(key, "count", 10L));
    }

    @Test
    @DisplayName("increment 浮点型自增")
    void testIncrementDouble() {
        String key = "test:hash:incr:double";
        redisHashOpsService.put(key, "price", 1.5);

        assertEquals(2.5, redisHashOpsService.increment(key, "price", 1.0), 0.001);
    }

    // ==================== 边界 ====================

    @Test
    @DisplayName("getAll 泛型对象")
    void testGetAllObject() {
        String key = "test:hash:user:map";
        User u1 = new User(1L, "张三", 20);
        User u2 = new User(2L, "李四", 21);

        Map<String, Object> map = new HashMap<>();
        map.put("u1", u1);
        map.put("u2", u2);
        redisHashOpsService.putAll(key, map);

        Map<String, User> result = redisHashOpsService.getAll(key, User.class);

        assertEquals(2, result.size());
        assertEquals(u1, result.get("u1"));
        assertEquals(u2, result.get("u2"));
    }


}
