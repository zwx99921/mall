package com.we.mall.common.sensitive.strategy.impl;

import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 密码脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class PasswordSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        return "******";
    }

}
