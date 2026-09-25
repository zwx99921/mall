package com.we.mall.common.sensitive.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 姓名脱敏
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class NameSensitiveStrategy implements SensitiveStrategy {
    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 2) {
            return value;
        }
        return value.charAt(0) + StrUtil.repeat('*', value.length() - 1);
    }
}
