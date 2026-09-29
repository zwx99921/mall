package com.we.mall.common.session.properties;

import com.we.mall.common.core.enums.DeviceType;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.SystemException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Session 配置
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.session")
public class SessionProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * session 默认过期时长（秒）
     */
    private long timeoutSeconds = 1800L;

    /**
     * 每次访问是否刷新 TTL（滑动过期）
     */
    private boolean slidingExpiration = true;

    /**
     * sessionId 长度
     */
    private int sessionIdLength = 32;

    /**
     * 各端独立配置
     */
    private Map<String, ClientConfig> clients = new LinkedHashMap<>();

    /**
     * 获取某个端的配置，不存在则抛异常
     */
    public ClientConfig getClient(String clientCode) {
        ClientConfig cfg = clients.get(clientCode);
        if (cfg == null) {
            throw SystemException.of(ResultCode.CONFIG_ERROR, "未配置的客户端类型: " + clientCode);
        }
        if (!StringUtils.hasText(cfg.getKeyPrefix())) {
            cfg.setKeyPrefix("session:" + clientCode + ":");
        }
        return cfg;
    }

    /**
     * 单端配置
     */
    @Data
    public static class ClientConfig {

        /**
         * key 前缀
         */
        private String keyPrefix;

        /**
         * 支持的设备类型
         */
        private List<DeviceType> deviceTypes = Arrays.asList(DeviceType.DESKTOP, DeviceType.PHONE, DeviceType.TABLET, DeviceType.MOBILE);

        /**
         * 同端是否互踢
         */
        private boolean sameDeviceKick = true;

        /**
         * 每端最大在线数（0 不限）
         */
        private int maxPerDevice = 0;

        /**
         * 是否允许未知设备
         */
        private boolean allowUnknownDevice = false;
    }

}
