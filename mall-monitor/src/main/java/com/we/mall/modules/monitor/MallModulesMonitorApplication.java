package com.we.mall.modules.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 监控服务启动类
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallModulesMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallModulesMonitorApplication.class, args);
    }

}
