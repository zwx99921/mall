package com.we.mall.common.sensitive.enums;

import lombok.Getter;

/**
 * 脱敏类型
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Getter
public enum SensitiveType {

    /**
     * 默认：全部替换
     */
    DEFAULT,

    /**
     * 手机号：138****5678
     */
    PHONE,

    /**
     * 身份证：110101********1234
     */
    ID_CARD,

    /**
     * 银行卡：6222 **** **** 0123
     */
    BANK_CARD,

    /**
     * 邮箱：z****@example.com
     */
    EMAIL,

    /**
     * 姓名：张*
     */
    NAME,

    /**
     * 地址：北京市朝阳区***
     */
    ADDRESS,

    /**
     * 密码：******
     */
    PASSWORD,

    /**
     * 自定义：由 @Sensitive 的 strategy 指定
     */
    CUSTOM

}
