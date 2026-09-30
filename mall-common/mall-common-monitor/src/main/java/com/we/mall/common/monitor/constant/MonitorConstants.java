package com.we.mall.common.monitor.constant;

/**
 * 监控常量
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public final class MonitorConstants {

    /**
     * Actuator 端点 ID
     */
    public static final String ENDPOINT_ID = "systemInfo";
    /**
     * Actuator 系统信息路径
     */
    public static final String ACTUATOR_SYSTEM_INFO_PATH = "/actuator/" + ENDPOINT_ID;

    private MonitorConstants() {
    }

}