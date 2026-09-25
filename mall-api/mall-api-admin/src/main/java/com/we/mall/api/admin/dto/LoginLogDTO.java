package com.we.mall.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 登录 IP
     */
    private String loginIp;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 登录方式
     */
    private String loginType;

    /**
     * 状态：1 成功 0 失败
     */
    private Integer status;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

}
