package com.we.mall.modules.monitor.controller.admin;

import com.we.mall.common.core.result.R;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.modules.monitor.model.response.ServiceInstanceResponse;
import com.we.mall.modules.monitor.model.response.ServiceOverviewResponse;
import com.we.mall.modules.monitor.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Tag(name = "监控", description = "服务监控")
@RestController
@RequestMapping("/admin/monitor")
@RequiredArgsConstructor
@Validated
public class MonitorController {

    private final MonitorService monitorService;

    @Operation(summary = "监控总览")
    @GetMapping("/overview")
    @RequiresPermission("admin:monitor:list")
    public R<List<ServiceOverviewResponse>> overview() {
        return R.ok(monitorService.overview());
    }

    @Operation(summary = "所有服务名")
    @GetMapping("/services")
    @RequiresPermission("admin:monitor:list")
    public R<List<String>> services() {
        return R.ok(monitorService.listServices());
    }

    @Operation(summary = "某服务的所有实例")
    @GetMapping("/services/{serviceName}/instances")
    @RequiresPermission("admin:monitor:view")
    public R<List<ServiceInstanceResponse>> instances(@PathVariable String serviceName) {
        return R.ok(monitorService.listInstances(serviceName));
    }

}
