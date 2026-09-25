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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisListOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis List 操作测试")
public class RedisListOpsServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisListOpsService redisListOpsService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== leftPush ====================

    @Test
    @DisplayName("leftPush 左侧入队")
    void testLeftPush() {
        String key = "test:list:left";
        redisListOpsService.leftPush(key, "a");
        redisListOpsService.leftPush(key, "b");

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("b", "a"), result);
    }

    @Test
    @DisplayName("leftPushAll 批量左侧入队")
    void testLeftPushAll() {
        String key = "test:list:leftAll";
        redisListOpsService.leftPushAll(key, Arrays.asList("a", "b", "c"));

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(3, result.size());
    }

    // ==================== rightPush ====================

    @Test
    @DisplayName("rightPush 右侧入队")
    void testRightPush() {
        String key = "test:list:right";
        redisListOpsService.rightPush(key, "a");
        redisListOpsService.rightPush(key, "b");

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("a", "b"), result);
    }

    @Test
    @DisplayName("rightPushAll 批量右侧入队")
    void testRightPushAll() {
        String key = "test:list:rightAll";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    // ==================== set ====================

    @Test
    @DisplayName("set 按索引设置值")
    void testSet() {
        String key = "test:list:set";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        redisListOpsService.set(key, 1, "B");

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("a", "B", "c"), result);
    }

    // ==================== range ====================

    @Test
    @DisplayName("range 获取指定区间")
    void testRange() {
        String key = "test:list:range";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c", "d", "e"));

        List<String> result = redisListOpsService.range(key, 1, 3, String.class);
        assertEquals(Arrays.asList("b", "c", "d"), result);
    }

    @Test
    @DisplayName("range -1 表示末尾")
    void testRangeToEnd() {
        String key = "test:list:range:end";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("range 不存在的 key 返回空 List")
    void testRangeNotExists() {
        List<String> result = redisListOpsService.range("test:list:not:exists", 0, -1, String.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("range 对象类型")
    void testRangeObject() {
        String key = "test:list:obj";
        User u1 = new User(1L, "张三", 20);
        User u2 = new User(2L, "李四", 21);
        redisListOpsService.rightPush(key, u1);
        redisListOpsService.rightPush(key, u2);

        List<User> result = redisListOpsService.range(key, 0, -1, User.class);

        assertEquals(2, result.size());
        assertEquals(u1, result.get(0));
        assertEquals(u2, result.get(1));
    }

    // ==================== index ====================

    @Test
    @DisplayName("index 获取指定索引元素")
    void testIndex() {
        String key = "test:list:index";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        assertEquals("b", redisListOpsService.index(key, 1, String.class));
    }

    @Test
    @DisplayName("index 越界返回 null")
    void testIndexOutOfRange() {
        String key = "test:list:index:out";
        redisListOpsService.rightPush(key, "a");

        assertNull(redisListOpsService.index(key, 99, String.class));
    }

    // ==================== size ====================

    @Test
    @DisplayName("size 获取列表长度")
    void testSize() {
        String key = "test:list:size";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        assertEquals(3L, redisListOpsService.size(key));
    }

    // ==================== leftPop / rightPop ====================

    @Test
    @DisplayName("leftPop 左侧弹出")
    void testLeftPop() {
        String key = "test:list:leftPop";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        assertEquals("a", redisListOpsService.leftPop(key, String.class));
        assertEquals("b", redisListOpsService.leftPop(key, String.class));
    }

    @Test
    @DisplayName("rightPop 右侧弹出")
    void testRightPop() {
        String key = "test:list:rightPop";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c"));

        assertEquals("c", redisListOpsService.rightPop(key, String.class));
        assertEquals("b", redisListOpsService.rightPop(key, String.class));
    }

    @Test
    @DisplayName("pop 空列表返回 null")
    void testPopEmpty() {
        String key = "test:list:pop:empty";
        assertNull(redisListOpsService.leftPop(key, String.class));
        assertNull(redisListOpsService.rightPop(key, String.class));
    }

    @Test
    @DisplayName("pop 对象类型")
    void testPopObject() {
        String key = "test:list:pop:obj";
        User u = new User(1L, "张三", 20);
        redisListOpsService.rightPush(key, u);

        User result = redisListOpsService.leftPop(key, User.class);
        assertEquals(u, result);
    }

    // ==================== remove ====================

    @Test
    @DisplayName("remove count > 0 从左往右删")
    void testRemovePositive() {
        String key = "test:list:remove:pos";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "a", "c", "a"));

        Long removed = redisListOpsService.remove(key, 2, "a");

        assertEquals(2L, removed);
        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("b", "c", "a"), result);
    }

    @Test
    @DisplayName("remove count < 0 从右往左删")
    void testRemoveNegative() {
        String key = "test:list:remove:neg";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "a", "c", "a"));

        Long removed = redisListOpsService.remove(key, -1, "a");

        assertEquals(1L, removed);
        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("a", "b", "a", "c"), result);
    }

    @Test
    @DisplayName("remove count = 0 全删")
    void testRemoveAll() {
        String key = "test:list:remove:all";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "a", "c", "a"));

        Long removed = redisListOpsService.remove(key, 0, "a");

        assertEquals(3L, removed);
        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("b", "c"), result);
    }

    // ==================== trim ====================

    @Test
    @DisplayName("trim 保留 [start, end] 区间")
    void testTrim() {
        String key = "test:list:trim";
        redisListOpsService.rightPushAll(key, Arrays.asList("a", "b", "c", "d", "e"));

        redisListOpsService.trim(key, 1, 3);

        List<String> result = redisListOpsService.range(key, 0, -1, String.class);
        assertEquals(Arrays.asList("b", "c", "d"), result);
    }

    // ==================== 队列场景 ====================

    @Test
    @DisplayName("队列：右进左出（FIFO）")
    void testQueue() {
        String key = "test:list:queue";
        redisListOpsService.rightPush(key, "msg1");
        redisListOpsService.rightPush(key, "msg2");
        redisListOpsService.rightPush(key, "msg3");

        assertEquals("msg1", redisListOpsService.leftPop(key, String.class));
        assertEquals("msg2", redisListOpsService.leftPop(key, String.class));
        assertEquals("msg3", redisListOpsService.leftPop(key, String.class));
    }

    @Test
    @DisplayName("栈：右进右出（LIFO）")
    void testStack() {
        String key = "test:list:stack";
        redisListOpsService.rightPush(key, "a");
        redisListOpsService.rightPush(key, "b");
        redisListOpsService.rightPush(key, "c");

        assertEquals("c", redisListOpsService.rightPop(key, String.class));
        assertEquals("b", redisListOpsService.rightPop(key, String.class));
        assertEquals("a", redisListOpsService.rightPop(key, String.class));
    }

}
