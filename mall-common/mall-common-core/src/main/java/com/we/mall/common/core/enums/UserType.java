package com.we.mall.common.core.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 用户类型枚举
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Getter
public enum UserType {

    ADMIN("admin", "后台管理用户"),
    MEMBER("member", "C端普通用户");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据 code 获取枚举信息
    public static UserType of(String code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

}
