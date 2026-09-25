package com.we.mall.common.session.enums;

import com.we.mall.common.core.enums.UserType;
import lombok.Getter;

/**
 * 客户端类型（第一维度）
 * <p>
 * C 端和管理端完全隔离，互不影响。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Getter
public enum ClientType {

    ADMIN("admin", UserType.ADMIN),
    MEMBER("member", UserType.MEMBER);

    /**
     * 编码，用于 key 前缀 / header 传输
     */
    private final String code;

    /**
     * 对应的用户类型
     */
    private final UserType userType;

    ClientType(String code, UserType userType) {
        this.code = code;
        this.userType = userType;
    }

    /**
     * 从 UserTyep  转
     */
    public static ClientType fromUserType(UserType userType) {
        if (userType == null) return null;
        for (ClientType clientType : values()) {
            if (clientType.userType == userType) {
                return clientType;
            }
        }
        return null;
    }

    /**
     * 按 code 或 name 反查
     */
    public static ClientType of(String code) {
        if (code == null) {
            return MEMBER;
        }
        for (ClientType clientType : values()) {
            if (clientType.code.equalsIgnoreCase(code)
                    || clientType.name().equalsIgnoreCase(code)) {
                return clientType;
            }
        }
        return MEMBER;
    }

    /**
     * 转成 UserType
     */
    public UserType toUserType() {
        return this.userType;
    }

}
