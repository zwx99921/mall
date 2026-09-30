package com.we.mall.modules.monitor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.monitor")
public class MonitorProperties {

    /**
     * 拉取实例信息线程池大小
     */
    private int fetchThreadPoolSize = 10;

    /**
     * 拉取实例信息队列容量
     */
    private int fetchQueueCapacity = 100;

    /**
     * 请求超时（毫秒）
     */
    private int fetchTimeoutMs = 5000;

    /**
     * 白名单：只监控匹配这些模式的服务（Ant 风格），为空则监控所有
     */
    private Set<String> includePatterns = new HashSet<>();

    /**
     * 排除名单：不监控匹配这些模式的服务（Ant 风格），优先级高于 includePatterns
     */
    private Set<String> excludePatterns = new HashSet<>();

}
