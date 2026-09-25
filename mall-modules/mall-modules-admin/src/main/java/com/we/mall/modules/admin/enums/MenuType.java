package com.we.mall.modules.admin.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 菜单类型
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Getter
public enum MenuType {

    /**
     * 目录
     */
    DIR(0, "目录"),

    /**
     * 菜单
     */
    MENU(1, "菜单"),

    /**
     * 按钮
     */
    BUTTON(2, "按钮");

    private final Integer code;

    private final String desc;

    MenuType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MenuType of(Integer code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equals(code))
                .findFirst()
                .orElse(null);
    }

}
