package com.we.mall.common.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话元信息
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 客户端类型：c / admin
     */
    private String clientType;

    /**
     * 设备类型：PC / H5 / APP / MINI
     */
    private String deviceType;

    /**
     * 设备名称，如 "iPhone 15 Pro"
     */
    private String deviceName;

    /**
     * 用户Id，存快照做关联
     */
    private Long userId;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

}
