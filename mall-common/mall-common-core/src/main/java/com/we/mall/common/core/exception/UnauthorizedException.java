package com.we.mall.common.core.exception;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 未授权异常 - HTTP 401
 * <p>
 * 未登录、Session 过期。
 * <p>
 * HTTP status 由 {@code GlobalExceptionHandler} 映射为 <b>401</b>。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    // ==================== 构造器 ====================

    public UnauthorizedException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public UnauthorizedException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public UnauthorizedException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    // ==================== getter ====================

    /**
     * 未登录
     */
    public static UnauthorizedException notLogin() {
        return new UnauthorizedException(ResultCode.UNAUTHORIZED);
    }

    // ==================== 静态工厂（常用场景） ====================

    public static UnauthorizedException notLogin(String message) {
        return new UnauthorizedException(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * Session 过期
     */
    public static UnauthorizedException sessionExpired() {
        return new UnauthorizedException(ResultCode.SESSION_EXPIRED);
    }

    /**
     * 通用工厂
     */
    public static UnauthorizedException of(ResultCode resultCode) {
        return new UnauthorizedException(resultCode);
    }

    public static UnauthorizedException of(ResultCode resultCode, String message) {
        return new UnauthorizedException(resultCode, message);
    }

    public Integer getCode() {
        return resultCode.getCode();
    }

}
