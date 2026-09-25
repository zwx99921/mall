package com.we.mall.modules.file.config;

import com.we.mall.modules.file.properties.FileProperties;
import com.we.mall.modules.file.storage.impl.LocalFileStorage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件服务配置类
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class MallModulesFileConfig {

    @Bean
    public LocalFileStorage localFileStorage(FileProperties properties) {
        return new LocalFileStorage(properties);
    }

}
