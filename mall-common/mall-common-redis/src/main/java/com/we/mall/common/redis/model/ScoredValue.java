package com.we.mall.common.redis.model;

import lombok.Getter;

/**
 * ZSet 带分数的值对象
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Getter
public class ScoredValue<T> {

    private final T value;
    private final double score;

    public ScoredValue(T value, double score) {
        this.value = value;
        this.score = score;
    }

}
