package com.we.mall.common.sensitive.strategy;

/**
 * 脱敏策略
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public interface SensitiveStrategy {

    /**
     * 脱敏
     *
     * @param value 原始值
     * @return 脱敏后的值
     */
    String desensitize(String value);

}
