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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisSetOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis Set 操作测试")
public class RedisSetOpsServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisSetOpsService redisSetOpsService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== add ====================

    @Test
    @DisplayName("add 添加单个元素")
    void testAddSingle() {
        String key = "test:set:add";
        Long added = redisSetOpsService.add(key, "a");

        assertEquals(1L, added);
        assertEquals(1L, redisSetOpsService.size(key));
    }

    @Test
    @DisplayName("add 添加多个元素")
    void testAddMultiple() {
        String key = "test:set:add:multi";
        Long added = redisSetOpsService.add(key, "a", "b", "c");

        assertEquals(3L, added);
        assertEquals(3L, redisSetOpsService.size(key));
    }

    @Test
    @DisplayName("add 重复元素不重复添加")
    void testAddDuplicate() {
        String key = "test:set:add:dup";
        redisSetOpsService.add(key, "a", "b");
        Long added = redisSetOpsService.add(key, "a", "c");

        assertEquals(1L, added);
        assertEquals(3L, redisSetOpsService.size(key));
    }

    // ==================== remove ====================

    @Test
    @DisplayName("remove 删除元素")
    void testRemove() {
        String key = "test:set:remove";
        redisSetOpsService.add(key, "a", "b", "c");

        Long removed = redisSetOpsService.remove(key, "a", "b");

        assertEquals(2L, removed);
        assertEquals(1L, redisSetOpsService.size(key));
        assertTrue(redisSetOpsService.isMember(key, "c"));
    }

    @Test
    @DisplayName("remove 不存在的元素返回 0")
    void testRemoveNotExists() {
        String key = "test:set:remove:not:exists";
        redisSetOpsService.add(key, "a");

        Long removed = redisSetOpsService.remove(key, "x");

        assertEquals(0L, removed);
    }

    // ==================== isMember ====================

    @Test
    @DisplayName("isMember 判断元素存在")
    void testIsMember() {
        String key = "test:set:member";
        redisSetOpsService.add(key, "a", "b");

        assertEquals(Boolean.TRUE, redisSetOpsService.isMember(key, "a"));
        assertEquals(Boolean.FALSE, redisSetOpsService.isMember(key, "x"));
    }

    // ==================== members ====================

    @Test
    @DisplayName("members 获取所有元素")
    void testMembers() {
        String key = "test:set:members";
        redisSetOpsService.add(key, "a", "b", "c");

        Set<String> result = redisSetOpsService.members(key, String.class);

        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    @DisplayName("members 不存在的 key 返回空 Set")
    void testMembersNotExists() {
        Set<String> result = redisSetOpsService.members("test:set:not:exists", String.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("members 对象类型")
    void testMembersObject() {
        String key = "test:set:members:obj";
        User u1 = new User(1L, "张三", 20);
        User u2 = new User(2L, "李四", 21);
        redisSetOpsService.add(key, u1, u2);

        Set<User> result = redisSetOpsService.members(key, User.class);

        assertEquals(2, result.size());
        assertTrue(result.contains(u1));
        assertTrue(result.contains(u2));
    }

    // ==================== size ====================

    @Test
    @DisplayName("size 获取集合大小")
    void testSize() {
        String key = "test:set:size";
        redisSetOpsService.add(key, "a", "b", "c");

        assertEquals(3L, redisSetOpsService.size(key));
    }

    // ==================== randomMember ====================

    @Test
    @DisplayName("randomMember 随机取一个元素")
    void testRandomMember() {
        String key = "test:set:random";
        redisSetOpsService.add(key, "a", "b", "c");

        String result = redisSetOpsService.randomMember(key, String.class);

        assertNotNull(result);
        assertTrue(result.equals("a") || result.equals("b") || result.equals("c"));
    }

    @Test
    @DisplayName("randomMember 空集合返回 null")
    void testRandomMemberEmpty() {
        assertNull(redisSetOpsService.randomMember("test:set:random:empty", String.class));
    }

    // ==================== intersect ====================

    @Test
    @DisplayName("intersect 求交集")
    void testIntersect() {
        String key1 = "test:set:intersect:1";
        String key2 = "test:set:intersect:2";
        redisSetOpsService.add(key1, "a", "b", "c");
        redisSetOpsService.add(key2, "b", "c", "d");

        Set<String> result = redisSetOpsService.intersect(key1, key2, String.class);

        assertEquals(2, result.size());
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    @DisplayName("intersect 无交集返回空 Set")
    void testIntersectEmpty() {
        String key1 = "test:set:intersect:empty:1";
        String key2 = "test:set:intersect:empty:2";
        redisSetOpsService.add(key1, "a");
        redisSetOpsService.add(key2, "b");

        Set<String> result = redisSetOpsService.intersect(key1, key2, String.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== union ====================

    @Test
    @DisplayName("union 求并集")
    void testUnion() {
        String key1 = "test:set:union:1";
        String key2 = "test:set:union:2";
        redisSetOpsService.add(key1, "a", "b");
        redisSetOpsService.add(key2, "b", "c");

        Set<String> result = redisSetOpsService.union(key1, key2, String.class);

        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：去重")
    void testDedup() {
        String key = "test:set:dedup";
        redisSetOpsService.add(key, "a", "b", "a", "c", "b");

        assertEquals(3L, redisSetOpsService.size(key));
    }

    @Test
    @DisplayName("场景：标签求共同好友")
    void testCommonFriends() {
        String user1 = "test:set:friend:1";
        String user2 = "test:set:friend:2";
        redisSetOpsService.add(user1, "u3", "u4", "u5");
        redisSetOpsService.add(user2, "u4", "u5", "u6");

        Set<String> common = redisSetOpsService.intersect(user1, user2, String.class);

        assertEquals(2, common.size());
        assertTrue(common.contains("u4"));
        assertTrue(common.contains("u5"));
    }


}
