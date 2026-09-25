package com.we.mall.common.swagger.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger 配置
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.swagger")
public class SwaggerProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 文档标题
     */
    private String title = "API 文档";

    /**
     * 描述
     */
    private String description = "";

    /**
     * 版本
     */
    private String version = "1.0.0";

    /**
     * 联系人
     */
    private String contactName = "";

    private String contactEmail = "";

    private String contactUrl = "";

}
