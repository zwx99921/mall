package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 邮箱脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class EmailSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        if (value == null || !value.contains("@")) {
            return value;
        }
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            return value;
        }
        return value.charAt(0) + "****" + value.substring(atIndex);
    }

}
