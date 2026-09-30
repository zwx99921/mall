package com.we.mall.modules.monitor.service.impl;

import com.we.mall.common.core.util.PathMatcherUtils;
import com.we.mall.modules.monitor.assembler.ServiceOverviewAssembler;
import com.we.mall.modules.monitor.client.InstanceClient;
import com.we.mall.modules.monitor.model.response.ServiceInstanceResponse;
import com.we.mall.modules.monitor.model.response.ServiceOverviewResponse;
import com.we.mall.modules.monitor.properties.MonitorProperties;
import com.we.mall.modules.monitor.service.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 监控服务实现
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final DiscoveryClient discoveryClient;
    private final InstanceClient instanceClient;
    private final ServiceOverviewAssembler overviewAssembler;
    private final MonitorProperties monitorProperties;
    private final ThreadPoolTaskExecutor monitorExecutor;

    // ==================== 服务列表 ====================

    @Override
    public List<String> listServices() {
        List<String> services = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(services)) {
            return Collections.emptyList();
        }

        Set<String> includes = monitorProperties.getIncludePatterns();
        Set<String> excludes = monitorProperties.getExcludePatterns();

        return services.stream()
                .filter(s -> CollectionUtils.isEmpty(includes)
                        || PathMatcherUtils.matchesAny(includes, s))
                .filter(s -> CollectionUtils.isEmpty(excludes)
                        || !PathMatcherUtils.matchesAny(excludes, s))
                .collect(Collectors.toList());
    }

    // ==================== 实例列表 ====================

    @Override
    public List<ServiceInstanceResponse> listInstances(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }

        List<CompletableFuture<ServiceInstanceResponse>> futures = instances.stream()
                .map(instance -> CompletableFuture.supplyAsync(
                        () -> instanceClient.fetchSystemInfo(instance), monitorExecutor))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ==================== 总览 ====================

    @Override
    public List<ServiceOverviewResponse> overview() {
        List<String> services = listServices();
        if (CollectionUtils.isEmpty(services)) {
            return Collections.emptyList();
        }

        List<CompletableFuture<ServiceOverviewResponse>> futures = services.stream()
                .map(name -> CompletableFuture.supplyAsync(() ->
                        overviewAssembler.assemble(name, listInstances(name)), monitorExecutor))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
