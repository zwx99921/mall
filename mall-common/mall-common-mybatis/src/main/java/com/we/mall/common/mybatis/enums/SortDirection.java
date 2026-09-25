package com.we.mall.common.mybatis.enums;

import lombok.Getter;

/**
 * @author we
 * @version 1.0
 * @date 2026-09-03
 * @description 排序方向枚举类
 */

@Getter
public enum SortDirection {

    /**
     * 升序
     */
    ASC("asc", "升序"),

    /**
     * 降序
     */
    DESC("desc", "降序");

    private final String value;
    private final String description;

    SortDirection(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static SortDirection fromValue(String value) {
        if (value == null) {
            return DESC;
        }
        for (SortDirection direction : values()) {
            if (direction.value.equalsIgnoreCase(value)) {
                return direction;
            }
        }
        return DESC;
    }

    public boolean isAsc() {
        return this == ASC;
    }

    public boolean isDesc() {
        return this == DESC;
    }

}
