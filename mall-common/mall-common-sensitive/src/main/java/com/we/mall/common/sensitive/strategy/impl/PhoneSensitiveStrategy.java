package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 手机号脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class PhoneSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 11) {
            return value;
        }
        return value.substring(0, 3) + "****" + value.substring(7);
    }

}
