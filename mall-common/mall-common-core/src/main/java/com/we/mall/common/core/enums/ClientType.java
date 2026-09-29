package com.we.mall.common.core.enums;

import lombok.Getter;

/**
 * 客户端类型
 *
 * @author we
 * @date 2026-09-29
 * @description
 */
@Getter
public enum ClientType {

    ADMIN("admin"),
    MEMBER("member");

    private final String code;

    ClientType(String code) {
        this.code = code;
    }

    /**
     * 按 code 或 name 反查
     */
    public static ClientType of(String code) {
        if (code == null) {
            return null;
        }
        for (ClientType t : values()) {
            if (t.code.equalsIgnoreCase(code) || t.name().equalsIgnoreCase(code)) {
                return t;
            }
        }
        return null;
    }
}
