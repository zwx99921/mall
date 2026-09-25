package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 地址脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class AddressSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 6) {
            return value;
        }
        return value.substring(0, 6) + "***";
    }

}
