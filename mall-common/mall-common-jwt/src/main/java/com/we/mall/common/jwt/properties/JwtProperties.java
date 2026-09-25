package com.we.mall.common.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.jwt")
public class JwtProperties {

    /**
     * 启动 jwt
     */
    private boolean enabled = true;

    /**
     * 管理端配置
     */
    private Config admin = new Config();

    /**
     * C 端配置
     */
    private Config member = new Config();

    @Data
    public static class Config {

        /**
         * 签名密钥，必须 >= 32 字符
         */
        private String secret;

        /**
         * access token 有效期（毫秒），默认 2 小时
         */
        private Long expire = 2 * 60 * 60 * 1000L;

        /**
         * refresh token 有效期（毫秒），默认 7 天
         */
        private Long refreshExpire = 7 * 24 * 60 * 60 * 1000L;

        /**
         * issuer，默认 "mall"
         */
        private String issuer = "mall";
    }

}
