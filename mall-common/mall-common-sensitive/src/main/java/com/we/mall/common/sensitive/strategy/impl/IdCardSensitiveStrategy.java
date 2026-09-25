package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 身份证脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class IdCardSensitiveStrategy implements SensitiveStrategy {


    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 18) {
            return value;
        }
        return value.substring(0, 6) + "********" + value.substring(14);
    }
}
