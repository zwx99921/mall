package com.we.mall.auth.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 启动类
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.we.mall.api")
public class MallAuthAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthAdminApplication.class, args);
    }

}