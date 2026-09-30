package com.we.mall.modules.monitor.service;

import com.we.mall.modules.monitor.model.response.ServiceInstanceResponse;
import com.we.mall.modules.monitor.model.response.ServiceOverviewResponse;

import java.util.List;

/**
 * 监控服务
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public interface MonitorService {

    /**
     * 监控总览
     */
    List<ServiceOverviewResponse> overview();

    /**
     * 所有服务名
     */
    List<String> listServices();

    /**
     * 某服务的实例列表
     */
    List<ServiceInstanceResponse> listInstances(String serviceName);
}
