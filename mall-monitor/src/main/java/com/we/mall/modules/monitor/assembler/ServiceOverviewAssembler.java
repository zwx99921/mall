package com.we.mall.modules.monitor.assembler;

import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import com.we.mall.modules.monitor.model.response.ServiceInstanceResponse;
import com.we.mall.modules.monitor.model.response.ServiceOverviewResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务总览组装器
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Component
public class ServiceOverviewAssembler {

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * 组装服务总览
     */
    public ServiceOverviewResponse assemble(String serviceName,
                                            List<ServiceInstanceResponse> instances) {
        ServiceOverviewResponse overview = new ServiceOverviewResponse();
        overview.setServiceName(serviceName);
        overview.setInstanceCount(instances.size());
        overview.setInstances(instances);

        if (instances.isEmpty()) {
            overview.setHealthyCount(0);
            overview.setUnhealthyCount(0);
            overview.setAvgCpu(0.0);
            overview.setAvgMemory(0.0);
            overview.setAvgDisk(0.0);
            return overview;
        }

        int healthy = 0;
        double sumCpu = 0, sumMemory = 0, sumDisk = 0;
        int cpuCount = 0, memoryCount = 0, diskCount = 0;

        for (ServiceInstanceResponse instance : instances) {
            SystemInfoResponse info = instance.getSystemInfo();
            if (info == null) {
                continue;
            }
            healthy++;

            if (info.getCpu() != null && info.getCpu().getUsagePercent() != null) {
                sumCpu += info.getCpu().getUsagePercent();
                cpuCount++;
            }
            if (info.getJvm() != null && info.getJvm().getUsagePercent() != null) {
                sumMemory += info.getJvm().getUsagePercent();
                memoryCount++;
            }
            if (info.getDisk() != null && info.getDisk().getUsagePercent() != null) {
                sumDisk += info.getDisk().getUsagePercent();
                diskCount++;
            }
        }

        overview.setHealthyCount(healthy);
        overview.setUnhealthyCount(instances.size() - healthy);
        overview.setAvgCpu(cpuCount > 0 ? round(sumCpu / cpuCount) : 0.0);
        overview.setAvgMemory(memoryCount > 0 ? round(sumMemory / memoryCount) : 0.0);
        overview.setAvgDisk(diskCount > 0 ? round(sumDisk / diskCount) : 0.0);
        return overview;
    }

}
