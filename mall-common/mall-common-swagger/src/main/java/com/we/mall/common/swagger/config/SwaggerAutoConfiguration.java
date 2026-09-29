package com.we.mall.common.swagger.config;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.swagger.converter.ModelConverterImpl;
import com.we.mall.common.swagger.properties.SwaggerProperties;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Swagger 自动配置
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@AutoConfiguration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "mall.swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    @Bean
    public OpenAPI openAPI(SwaggerProperties properties) {
        // 联系人
        Contact contact = new Contact()
                .name(properties.getContactName())
                .email(properties.getContactEmail())
                .url(properties.getContactUrl());

        // 文档信息
        Info info = new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(contact);

        // 认证方式（Bearer Token）
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HeaderConstants.HEADER_AUTHORIZATION);   // Authorization

        // 全局认证
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(HeaderConstants.HEADER_AUTHORIZATION);

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes(HeaderConstants.HEADER_AUTHORIZATION, securityScheme))
                .addSecurityItem(securityRequirement);
    }

    @Bean
    public ModelConverter modelConverter() {
        return new ModelConverterImpl();
    }


}
