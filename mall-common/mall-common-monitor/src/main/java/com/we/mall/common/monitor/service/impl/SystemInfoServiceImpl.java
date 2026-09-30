package com.we.mall.common.monitor.service.impl;

import com.we.mall.common.monitor.collector.SystemInfoCollector;
import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import com.we.mall.common.monitor.service.SystemInfoService;

/**
 * 系统信息服务实现
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public class SystemInfoServiceImpl implements SystemInfoService {

    private final SystemInfoCollector systemInfoCollector;

    public SystemInfoServiceImpl(SystemInfoCollector systemInfoCollector) {
        this.systemInfoCollector = systemInfoCollector;
    }

    @Override
    public SystemInfoResponse info() {
        SystemInfoResponse response = new SystemInfoResponse();
        response.setApp(systemInfoCollector.collectApp());
        response.setJvm(systemInfoCollector.collectJvm());
        response.setCpu(systemInfoCollector.collectCpu());
        response.setOs(systemInfoCollector.collectOs());
        response.setDisk(systemInfoCollector.collectDisk());
        return response;
    }
}
