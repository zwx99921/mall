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

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisStringOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis String 操作测试")
public class RedisStringOpsServiceTest {

    @Autowired
    private RedisStringOpsService redisStringOpsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== set / get ====================

    @Test
    @DisplayName("set + get 单对象")
    void testSetAndGetObject() {
        String key = "test:user:1";
        User user = new User(1L, "张三", 20);

        redisStringOpsService.set(key, user);
        User result = redisStringOpsService.get(key, User.class);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("set + get 指定过期时间")
    void testSetWithTimeout() throws InterruptedException {
        String key = "test:user:ttl";
        redisStringOpsService.set(key, new User(1L, "张三", 20), 1, TimeUnit.SECONDS);

        assertNotNull(redisStringOpsService.get(key, User.class));

        Thread.sleep(1100);

        assertNull(redisStringOpsService.get(key, User.class));
    }

    @Test
    @DisplayName("get 不存在的 key 返回 null")
    void testGetNotExists() {
        assertNull(redisStringOpsService.get("test:not:exists", User.class));
    }

    // ==================== setWithRandomTtl ====================

    @Test
    @DisplayName("setWithRandomTtl 设置成功")
    void testSetWithRandomTtl() {
        String key = "test:random:ttl";
        redisStringOpsService.setWithRandomTtl(key, new User(1L, "张三", 20), 60, 30);

        assertNotNull(redisStringOpsService.get(key, User.class));
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertNotNull(expire);
        // 实际 TTL ∈ [60, 90)
        assertTrue(expire >= 60 && expire < 90, "TTL 应在 [60, 90)，实际：" + expire);
    }
    // ==================== setIfAbsent ====================

    @Test
    @DisplayName("setIfAbsent 首次成功，再次失败")
    void testSetIfAbsent() {
        String key = "test:absent";
        Boolean first = redisStringOpsService.setIfAbsent(key, "v1", 60, TimeUnit.SECONDS);
        Boolean second = redisStringOpsService.setIfAbsent(key, "v2", 60, TimeUnit.SECONDS);

        assertEquals(Boolean.TRUE, first);
        assertEquals(Boolean.FALSE, second);
        assertEquals("v1", redisStringOpsService.get(key, String.class));
    }

    // ==================== setIfPresent ====================

    @Test
    @DisplayName("setIfPresent key 不存在时失败")
    void testSetIfPresentNotExists() {
        Boolean result = redisStringOpsService.setIfPresent("test:present:not:exists", "v");
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    @DisplayName("setIfPresent key 存在时成功")
    void testSetIfPresentExists() {
        String key = "test:present";
        redisStringOpsService.set(key, "old");
        Boolean result = redisStringOpsService.setIfPresent(key, "new");

        assertEquals(Boolean.TRUE, result);
        assertEquals("new", redisStringOpsService.get(key, String.class));
    }

    // ==================== multiSet ====================

    @Test
    @DisplayName("multiSet 批量写入")
    void testMultiSet() {
        Map<String, Object> map = new HashMap<>();
        map.put("test:multi:1", "v1");
        map.put("test:multi:2", "v2");
        map.put("test:multi:3", "v3");

        redisStringOpsService.multiSet(map);

        assertEquals("v1", redisStringOpsService.get("test:multi:1", String.class));
        assertEquals("v2", redisStringOpsService.get("test:multi:2", String.class));
        assertEquals("v3", redisStringOpsService.get("test:multi:3", String.class));
    }

    // ==================== 泛型对象 ====================

    @Test
    @DisplayName("set + get 泛型对象 Result<User>")
    void testGetTypeReference() {
        String key = "test:result";
        R<User> result = R.ok(new User(1L, "张三", 20));

        redisStringOpsService.set(key, result);
        R<User> cached = redisStringOpsService.get(key, new TypeReference<R<User>>() {
        });

        assertNotNull(cached);
        assertEquals(ResultCode.SUCCESS.getCode(), cached.getCode());
        assertEquals(ResultCode.SUCCESS.getMessage(), cached.getMessage());
        assertNotNull(cached.getData());
        assertEquals("张三", cached.getData().getName());
    }

    // ==================== List ====================

    @Test
    @DisplayName("set + getList")
    void testGetList() {
        String key = "test:user:list";
        List<User> users = Arrays.asList(
                new User(1L, "张三", 20),
                new User(2L, "李四", 21),
                new User(3L, "王五", 22)
        );

        redisStringOpsService.set(key, users);
        List<User> result = redisStringOpsService.getList(key, User.class);

        assertEquals(3, result.size());
        assertEquals(users, result);
    }

    @Test
    @DisplayName("getList 不存在的 key 返回空 List")
    void testGetListNotExists() {
        List<User> result = redisStringOpsService.getList("test:list:not:exists", User.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Set ====================

    @Test
    @DisplayName("set + getSet")
    void testGetSet() {
        String key = "test:user:set";
        Set<User> users = new LinkedHashSet<>(Arrays.asList(
                new User(1L, "张三", 20),
                new User(2L, "李四", 21)
        ));

        redisStringOpsService.set(key, users);
        Set<User> result = redisStringOpsService.getSet(key, User.class);

        assertEquals(2, result.size());
        assertTrue(result.contains(new User(1L, "张三", 20)));
        assertTrue(result.contains(new User(2L, "李四", 21)));
    }

    // ==================== Map ====================

    @Test
    @DisplayName("set + getMap")
    void testGetMap() {
        String key = "test:user:map";
        Map<String, User> map = new LinkedHashMap<>();
        map.put("u1", new User(1L, "张三", 20));
        map.put("u2", new User(2L, "李四", 21));

        redisStringOpsService.set(key, map);
        Map<String, User> result = redisStringOpsService.getMap(key, String.class, User.class);

        assertEquals(2, result.size());
        assertEquals(new User(1L, "张三", 20), result.get("u1"));
        assertEquals(new User(2L, "李四", 21), result.get("u2"));
    }

    // ==================== multiGet ====================

    @Test
    @DisplayName("multiGet 批量读取，顺序一致")
    void testMultiGet() {
        redisStringOpsService.set("test:multiGet:1", new User(1L, "张三", 20));
        redisStringOpsService.set("test:multiGet:2", new User(2L, "李四", 21));
        redisStringOpsService.set("test:multiGet:3", new User(3L, "王五", 22));

        List<String> keys = Arrays.asList(
                "test:multiGet:1", "test:multiGet:2", "test:multiGet:3");

        List<User> result = redisStringOpsService.multiGet(keys, User.class);

        assertEquals(3, result.size());
        assertEquals("张三", result.get(0).getName());
        assertEquals("李四", result.get(1).getName());
        assertEquals("王五", result.get(2).getName());
    }

    @Test
    @DisplayName("multiGet 空集合返回空 List")
    void testMultiGetEmpty() {
        List<User> result = redisStringOpsService.multiGet(Collections.emptyList(), User.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== 数值 ====================

    @Test
    @DisplayName("increment 长整型自增")
    void testIncrementLong() {
        String key = "test:counter:long";
        redisStringOpsService.set(key, 100L);

        assertEquals(101L, redisStringOpsService.increment(key, 1));
        assertEquals(111L, redisStringOpsService.increment(key, 10));
    }

    @Test
    @DisplayName("increment 浮点型自增")
    void testIncrementDouble() {
        String key = "test:counter:double";
        redisStringOpsService.set(key, 1.5);

        assertEquals(2.5, redisStringOpsService.increment(key, 1.0), 0.001);
    }

    @Test
    @DisplayName("decrement 长整型自减")
    void testDecrement() {
        String key = "test:counter:dec";
        redisStringOpsService.set(key, 100L);

        assertEquals(99L, redisStringOpsService.decrement(key, 1));
        assertEquals(89L, redisStringOpsService.decrement(key, 10));
    }

    // ==================== 边界 ====================

    @Test
    @DisplayName("set null 值")
    void testSetNull() {
        String key = "test:null";
        redisStringOpsService.set(key, null);

        Object raw = redisTemplate.opsForValue().get(key);
        assertNull(raw);
    }

    @Test
    @DisplayName("set 空字符串")
    void testSetEmptyString() {
        String key = "test:empty";
        redisStringOpsService.set(key, "");

        assertEquals("", redisStringOpsService.get(key, String.class));
    }


}
