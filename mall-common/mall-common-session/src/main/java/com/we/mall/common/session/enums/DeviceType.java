package com.we.mall.common.session.enums;

/**
 * 设备类型（第二维度）
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public enum DeviceType {

    PC, H5, APP, MINI, UNKNOWN;

    public static DeviceType of(String v) {
        if (v == null) {
            return UNKNOWN;
        }
        try {
            return valueOf(v.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
