package com.we.mall.modules.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件服务启动类
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallModulesFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallModulesFileApplication.class, args);
    }

}
