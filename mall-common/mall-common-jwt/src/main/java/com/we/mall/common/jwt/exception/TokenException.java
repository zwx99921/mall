package com.we.mall.common.jwt.exception;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.UnauthorizedException;

/**
 * JWT 异常
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
public class TokenException extends UnauthorizedException {

    private TokenException(ResultCode resultCode) {
        super(resultCode);
    }

    private TokenException(ResultCode resultCode, String msg) {
        super(resultCode, msg);
    }

    public static TokenException of(ResultCode resultCode) {
        return new TokenException(resultCode);
    }

    public static TokenException of(ResultCode resultCode, String msg) {
        return new TokenException(resultCode, msg);
    }

    /**
     * token 过期
     */
    public static TokenException expired() {
        return new TokenException(ResultCode.TOKEN_EXPIRED);
    }

    /**
     * token 无效
     */
    public static TokenException invalid() {
        return new TokenException(ResultCode.TOKEN_INVALID);
    }

    /**
     * token 无效，带原因
     */
    public static TokenException invalid(String msg) {
        return new TokenException(ResultCode.TOKEN_INVALID, msg);
    }

}
