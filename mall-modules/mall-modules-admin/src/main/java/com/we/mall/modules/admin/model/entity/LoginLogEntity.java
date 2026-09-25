package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.we.mall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志
 *
 * @author we
 * @date 2026-09-22
 * @description
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_login_log")
public class LoginLogEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

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
