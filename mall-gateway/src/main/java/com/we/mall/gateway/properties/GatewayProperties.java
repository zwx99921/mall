package com.we.mall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@Data
@ConfigurationProperties(prefix = "mall.gateway")
public class GatewayProperties {

    /**
     * 免认证路径，支持 Ant 风格
     */
    private List<String> whiteList = new ArrayList<>();

    private List<String> docWhiteList = new ArrayList<>();

    private List<String> monitorWhiteList = new ArrayList<>();

    public List<String> getAllWhiteList() {
        List<String> all = new ArrayList<>();
        all.addAll(whiteList);
        all.addAll(docWhiteList);
        all.addAll(monitorWhiteList);
        return all;
    }
}
