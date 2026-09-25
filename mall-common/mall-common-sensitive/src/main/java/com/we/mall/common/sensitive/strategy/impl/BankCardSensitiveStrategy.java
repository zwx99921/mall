package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 银行卡脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class BankCardSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 16) {
            return value;
        }
        return value.substring(0, 4) + " **** **** " + value.substring(value.length() - 4);
    }
}
