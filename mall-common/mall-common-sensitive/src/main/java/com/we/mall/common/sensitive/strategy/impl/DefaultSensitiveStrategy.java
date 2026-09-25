package com.we.mall.common.sensitive.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

/**
 * 默认脱敏策略：全部替换为 *
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class DefaultSensitiveStrategy implements SensitiveStrategy {

    @Override
    public String desensitize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return StrUtil.repeat('*', value.length());
    }

}
