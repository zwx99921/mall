package com.we.mall.modules.monitor.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务监控总览响应
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Schema(description = "服务监控总览响应")
@Data
public class ServiceOverviewResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "服务名")
    private String serviceName;

    @Schema(description = "实例数")
    private Integer instanceCount;

    @Schema(description = "健康数")
    private Integer healthyCount;

    @Schema(description = "异常数")
    private Integer unhealthyCount;

    @Schema(description = "平均 CPU（%）")
    private Double avgCpu;

    @Schema(description = "平均内存（%）")
    private Double avgMemory;

    @Schema(description = "平均磁盘（%）")
    private Double avgDisk;

    @Schema(description = "实例列表")
    private List<ServiceInstanceResponse> instances;

}
