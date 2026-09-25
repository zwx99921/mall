package com.we.mall.common.web.util;

import cn.hutool.core.util.StrUtil;

/**
 * 请求头 工具类
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public final class HeaderUtils {

    private HeaderUtils() {
    }

    /**
     * 从请求头解析设备类型
     *
     * @param defaultType 缺省值
     * @return 设备类型（大写）
     */
    public static String resolve(String headerName, String defaultType) {
        String header = ServletUtils.getHeader(headerName);
        if (StrUtil.isBlank(header)) {
            return defaultType;
        }
        return header.toUpperCase();
    }

}
