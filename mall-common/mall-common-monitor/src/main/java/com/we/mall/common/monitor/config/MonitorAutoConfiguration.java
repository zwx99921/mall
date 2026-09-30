package com.we.mall.common.monitor.config;

import com.we.mall.common.monitor.collector.SystemInfoCollector;
import com.we.mall.common.monitor.endpoint.SystemInfoEndpoint;
import com.we.mall.common.monitor.service.SystemInfoService;
import com.we.mall.common.monitor.service.impl.SystemInfoServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 监控自动配置
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@AutoConfiguration
public class MonitorAutoConfiguration {

    @Bean
    public SystemInfoCollector systemInfoCollector(@Value("${spring.application.name:unknown}") String appName,
                                                   @Value("${app.version:1.0.0}") String appVersion) {
        return new SystemInfoCollector(appName, appVersion);
    }

    @Bean
    public SystemInfoService systemInfoService(SystemInfoCollector systemInfoCollector) {
        return new SystemInfoServiceImpl(systemInfoCollector);
    }

    @Bean
    public SystemInfoEndpoint systemInfoEndpoint(SystemInfoService systemInfoService) {
        return new SystemInfoEndpoint(systemInfoService);
    }

}
