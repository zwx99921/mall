package com.we.mall.modules.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 管理服务启动类
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallModulesAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallModulesAdminApplication.class, args);
    }

}
