package com.we.mall.common.core.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 用户状态
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Getter
public enum UserStatus {

    DISABLE(0, "禁用"),
    NORMAL(1, "正常");

    private final Integer code;
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatus of(Integer code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isDisabled() {
        return this == DISABLE;
    }

}
