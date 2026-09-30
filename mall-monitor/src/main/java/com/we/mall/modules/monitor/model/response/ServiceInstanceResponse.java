package com.we.mall.modules.monitor.model.response;

import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务实例响应
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Schema(description = "服务实例响应")
@Data
public class ServiceInstanceResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "服务名")
    private String serviceName;

    @Schema(description = "实例 ID")
    private String instanceId;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "系统信息")
    private SystemInfoResponse systemInfo;

}
