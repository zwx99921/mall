package com.we.mall.common.jwt.enums;

/**
 * Token 类型
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public enum TokenType {

    /**
     * 访问令牌，短期
     */
    ACCESS,
    /**
     * 刷新令牌，长期
     */
    REFRESH

}
