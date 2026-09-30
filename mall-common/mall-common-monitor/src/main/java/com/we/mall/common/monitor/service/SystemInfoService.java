package com.we.mall.common.monitor.service;

import com.we.mall.common.monitor.model.response.SystemInfoResponse;

/**
 * 系统信息服务
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public interface SystemInfoService {

    /**
     * 获取当前实例的系统信息
     */
    SystemInfoResponse info();

}
