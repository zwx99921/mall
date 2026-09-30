package com.we.mall.common.monitor.endpoint;

import com.we.mall.common.monitor.constant.MonitorConstants;
import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import com.we.mall.common.monitor.service.SystemInfoService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * 系统信息 Actuator 端点
 * <p>
 * 访问：GET /actuator/systemInfo
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Endpoint(id = MonitorConstants.ENDPOINT_ID)
public class SystemInfoEndpoint {

    private final SystemInfoService systemInfoService;

    public SystemInfoEndpoint(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @ReadOperation
    public SystemInfoResponse info() {
        return systemInfoService.info();
    }

}
