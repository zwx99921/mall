package com.we.mall.auth.admin.enums;

import lombok.Getter;

/**
 * 登录方式
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Getter
public enum LoginType {

    PASSWORD("密码登录"),
    CAPTCHA("验证码登录"),
    WECHAT("微信登录"),
    SMS("短信登录"),
    ;

    private final String desc;

    LoginType(String desc) {
        this.desc = desc;
    }

}
