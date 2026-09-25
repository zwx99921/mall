package com.we.mall.common.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.redis")
public class RedisProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 空值缓存时长（秒），防穿透
     * <p>
     * loader 返回 null 时，写入空值标记的 TTL。
     */
    private long nullValueTtlSeconds = 60L;

    /**
     * Redisson 连接池配置
     */
    private RedissonProperties redisson = new RedissonProperties();

    @Data
    public static class RedissonProperties {

        /**
         * 是否启用 Redisson（分布式锁）
         */
        private boolean enabled = false;

        /**
         * Redis host
         */
        private String host;

        /**
         * Redis port
         */
        private int port;

        /**
         * Redis password
         */
        private String password;

        /**
         * Redis database
         */
        private int database;

        /**
         * 连接池大小
         */
        private int poolSize = 16;

        /**
         * 最小空闲连接
         */
        private int minIdleSize = 4;
    }

}
