package com.we.mall.common.core.enums;

import lombok.Getter;

/**
 * 设备类型
 *
 * @author we
 * @date 2026-09-29
 * @description
 */
@Getter
public enum DeviceType {

    DESKTOP("Desktop", "桌面"),
    PHONE("Phone", "手机"),
    TABLET("Tablet", "平板"),
    MOBILE("Mobile", "移动设备"),
    WATCH("Watch", "手表"),
    TV("TV", "电视"),
    CONSOLE("Console", "游戏机"),
    CAR("Car", "车机"),
    ROBOT("Robot", "机器人"),
    ROBOT_MOBILE("Robot Mobile", "移动端机器人"),
    UNKNOWN("Unknown", "未知");

    private final String code;

    private final String desc;

    DeviceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 严格解析，未知返回 null
     */
    public static DeviceType of(String v) {
        if (v == null) {
            return null;
        }
        for (DeviceType t : values()) {
            if (t.code.equalsIgnoreCase(v) || t.name().equalsIgnoreCase(v)) {
                return t;
            }
        }
        return null;
    }

}
