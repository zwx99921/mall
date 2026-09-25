package com.we.mall.common.sensitive.factory;

import com.we.mall.common.sensitive.enums.SensitiveType;
import com.we.mall.common.sensitive.strategy.SensitiveStrategy;
import com.we.mall.common.sensitive.strategy.impl.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * 脱敏策略工厂
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public final class SensitiveStrategyFactory {

    private static final Map<SensitiveType, SensitiveStrategy> STRATEGY_MAP = new EnumMap<>(SensitiveType.class);

    static {
        STRATEGY_MAP.put(SensitiveType.DEFAULT, new DefaultSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.PHONE, new PhoneSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.ID_CARD, new IdCardSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.BANK_CARD, new BankCardSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.EMAIL, new EmailSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.NAME, new NameSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.ADDRESS, new AddressSensitiveStrategy());
        STRATEGY_MAP.put(SensitiveType.PASSWORD, new PasswordSensitiveStrategy());
    }

    private SensitiveStrategyFactory() {
    }

    /**
     * 按类型拿策略
     */
    public static SensitiveStrategy getStrategy(SensitiveType type) {
        if (type == null) {
            return STRATEGY_MAP.get(SensitiveType.DEFAULT);
        }
        return STRATEGY_MAP.getOrDefault(type, STRATEGY_MAP.get(SensitiveType.DEFAULT));
    }

}
