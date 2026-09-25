package com.we.mall.common.redis.service;

import com.we.mall.common.redis.TestApplication;
import com.we.mall.common.redis.model.ScoredValue;
import com.we.mall.common.redis.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisZSetOpsService 单元测试
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Redis ZSet 操作测试")
public class RedisZSetOpsServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisZSetOpsService redisZSetOpsService;

    @BeforeEach
    void cleanRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== add / score ====================

    @Test
    @DisplayName("add 新增元素，返回 true")
    void testAddNew() {
        String key = "test:zset:add";
        Boolean result = redisZSetOpsService.add(key, "a", 1.0);

        assertEquals(Boolean.TRUE, result);
        assertEquals(1.0, redisZSetOpsService.score(key, "a"), 0.001);
    }

    @Test
    @DisplayName("add 已存在元素，返回 false 并更新分数")
    void testAddUpdate() {
        String key = "test:zset:add:update";
        redisZSetOpsService.add(key, "a", 1.0);
        Boolean result = redisZSetOpsService.add(key, "a", 5.0);

        assertEquals(Boolean.FALSE, result);
        assertEquals(5.0, redisZSetOpsService.score(key, "a"), 0.001);
    }

    @Test
    @DisplayName("score 不存在的元素返回 null")
    void testScoreNotExists() {
        assertNull(redisZSetOpsService.score("test:zset:score:not:exists", "x"));
    }

    // ==================== remove ====================

    @Test
    @DisplayName("remove 删除单个元素")
    void testRemoveSingle() {
        String key = "test:zset:remove";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);

        Long removed = redisZSetOpsService.remove(key, "a");

        assertEquals(1L, removed);
        assertEquals(1L, redisZSetOpsService.size(key));
    }

    @Test
    @DisplayName("remove 删除多个元素")
    void testRemoveMultiple() {
        String key = "test:zset:remove:multi";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);

        Long removed = redisZSetOpsService.remove(key, "a", "b");

        assertEquals(2L, removed);
        assertEquals(1L, redisZSetOpsService.size(key));
    }

    // ==================== incrementScore ====================

    @Test
    @DisplayName("incrementScore 增加分数")
    void testIncrementScore() {
        String key = "test:zset:incr";
        redisZSetOpsService.add(key, "a", 10.0);

        Double result = redisZSetOpsService.incrementScore(key, "a", 5.0);

        assertEquals(15.0, result, 0.001);
    }

    @Test
    @DisplayName("incrementScore 元素不存在时从 0 开始")
    void testIncrementScoreNew() {
        String key = "test:zset:incr:new";

        Double result = redisZSetOpsService.incrementScore(key, "a", 5.0);

        assertEquals(5.0, result, 0.001);
    }

    // ==================== rank / reverseRank ====================

    @Test
    @DisplayName("rank 正序排名（从 0 开始）")
    void testRank() {
        String key = "test:zset:rank";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);

        assertEquals(0L, redisZSetOpsService.rank(key, "a"));
        assertEquals(1L, redisZSetOpsService.rank(key, "b"));
        assertEquals(2L, redisZSetOpsService.rank(key, "c"));
    }

    @Test
    @DisplayName("reverseRank 倒序排名")
    void testReverseRank() {
        String key = "test:zset:reverseRank";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);

        assertEquals(0L, redisZSetOpsService.reverseRank(key, "c"));
        assertEquals(1L, redisZSetOpsService.reverseRank(key, "b"));
        assertEquals(2L, redisZSetOpsService.reverseRank(key, "a"));
    }

    @Test
    @DisplayName("rank 不存在的元素返回 null")
    void testRankNotExists() {
        assertNull(redisZSetOpsService.rank("test:zset:rank:not:exists", "x"));
    }

    // ==================== range ====================

    @Test
    @DisplayName("range 按排名取元素（正序）")
    void testRange() {
        String key = "test:zset:range";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);
        redisZSetOpsService.add(key, "d", 4.0);

        List<String> result = redisZSetOpsService.range(key, 0, 1, String.class);

        assertEquals(2, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
    }

    @Test
    @DisplayName("range -1 表示末尾")
    void testRangeToEnd() {
        String key = "test:zset:range:end";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);

        List<String> result = redisZSetOpsService.range(key, 0, -1, String.class);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("range 不存在的 key 返回空 List")
    void testRangeNotExists() {
        List<String> result = redisZSetOpsService.range("test:zset:not:exists", 0, -1, String.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("range 对象类型")
    void testRangeObject() {
        String key = "test:zset:range:obj";
        User u1 = new User(1L, "张三");
        User u2 = new User(2L, "李四");
        redisZSetOpsService.add(key, u1, 1.0);
        redisZSetOpsService.add(key, u2, 2.0);

        List<User> result = redisZSetOpsService.range(key, 0, -1, User.class);

        assertEquals(2, result.size());
        assertEquals(u1, result.get(0));
        assertEquals(u2, result.get(1));
    }

    // ==================== rangeWithScores ====================

    @Test
    @DisplayName("rangeWithScores 带分数返回")
    void testRangeWithScores() {
        String key = "test:zset:rangeWithScores";
        redisZSetOpsService.add(key, "a", 1.5);
        redisZSetOpsService.add(key, "b", 2.5);
        redisZSetOpsService.add(key, "c", 3.5);

        List<ScoredValue<String>> result = redisZSetOpsService.rangeWithScores(key, 0, -1, String.class);

        assertEquals(3, result.size());
        assertEquals("a", result.get(0).getValue());
        assertEquals(1.5, result.get(0).getScore(), 0.001);
        assertEquals("b", result.get(1).getValue());
        assertEquals(2.5, result.get(1).getScore(), 0.001);
        assertEquals("c", result.get(2).getValue());
        assertEquals(3.5, result.get(2).getScore(), 0.001);
    }

    @Test
    @DisplayName("rangeWithScores 不存在的 key 返回空 List")
    void testRangeWithScoresNotExists() {
        List<ScoredValue<String>> result = redisZSetOpsService.rangeWithScores(
                "test:zset:rws:not:exists", 0, -1, String.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== rangeByScore ====================

    @Test
    @DisplayName("rangeByScore 按分数区间取元素")
    void testRangeByScore() {
        String key = "test:zset:rangeByScore";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);
        redisZSetOpsService.add(key, "d", 4.0);

        List<String> result = redisZSetOpsService.rangeByScore(key, 2.0, 3.0, String.class);

        assertEquals(2, result.size());
        assertEquals("b", result.get(0));
        assertEquals("c", result.get(1));
    }

    @Test
    @DisplayName("rangeByScore 无匹配返回空 List")
    void testRangeByScoreEmpty() {
        String key = "test:zset:rbs:empty";
        redisZSetOpsService.add(key, "a", 1.0);

        List<String> result = redisZSetOpsService.rangeByScore(key, 100.0, 200.0, String.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== count ====================

    @Test
    @DisplayName("count 统计分数区间元素数量")
    void testCount() {
        String key = "test:zset:count";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);
        redisZSetOpsService.add(key, "d", 4.0);

        assertEquals(2L, redisZSetOpsService.count(key, 2.0, 3.0));
        assertEquals(4L, redisZSetOpsService.count(key, 0.0, 10.0));
    }

    // ==================== size ====================

    @Test
    @DisplayName("size 获取集合大小")
    void testSize() {
        String key = "test:zset:size";
        redisZSetOpsService.add(key, "a", 1.0);
        redisZSetOpsService.add(key, "b", 2.0);
        redisZSetOpsService.add(key, "c", 3.0);

        assertEquals(3L, redisZSetOpsService.size(key));
    }

    // ==================== 场景 ====================

    @Test
    @DisplayName("场景：排行榜 Top N")
    void testLeaderboard() {
        String key = "test:zset:leaderboard";
        redisZSetOpsService.add(key, "user1", 100);
        redisZSetOpsService.add(key, "user2", 200);
        redisZSetOpsService.add(key, "user3", 300);
        redisZSetOpsService.add(key, "user4", 400);
        redisZSetOpsService.add(key, "user5", 500);

        List<ScoredValue<String>> top3 = redisZSetOpsService.rangeWithScores(key, 0, 2, String.class);

        assertEquals(3, top3.size());
        assertEquals("user1", top3.get(0).getValue());
        assertEquals(100.0, top3.get(0).getScore(), 0.001);
        assertEquals("user2", top3.get(1).getValue());
        assertEquals("user3", top3.get(2).getValue());
    }

    @Test
    @DisplayName("场景：按分数区间筛选")
    void testScoreFilter() {
        String key = "test:zset:filter";
        redisZSetOpsService.add(key, "u1", 10);
        redisZSetOpsService.add(key, "u2", 50);
        redisZSetOpsService.add(key, "u3", 80);
        redisZSetOpsService.add(key, "u4", 95);

        List<String> excellent = redisZSetOpsService.rangeByScore(key, 90, 100, String.class);

        assertEquals(1, excellent.size());
        assertEquals("u4", excellent.get(0));
    }


}
