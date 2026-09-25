package com.we.mall.common.core.exception;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 未授权异常 - HTTP 403
 * <p>
 * 已登录，但缺少角色 / 权限。
 * <p>
 * HTTP status 由 {@code GlobalExceptionHandler} 映射为 <b>403</b>。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    // ==================== 构造器 ====================

    public ForbiddenException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ForbiddenException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ForbiddenException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    // ==================== getter ====================

    /**
     * 无权限（默认）
     */
    public static ForbiddenException of() {
        return new ForbiddenException(ResultCode.FORBIDDEN);
    }

    // ==================== 静态工厂 ====================

    /**
     * 无权限，自定义提示
     */
    public static ForbiddenException of(String message) {
        return new ForbiddenException(ResultCode.FORBIDDEN, message);
    }

    /**
     * 通用工厂
     */
    public static ForbiddenException of(ResultCode resultCode) {
        return new ForbiddenException(resultCode);
    }

    public static ForbiddenException of(ResultCode resultCode, String message) {
        return new ForbiddenException(resultCode, message);
    }

    public Integer getCode() {
        return resultCode.getCode();
    }

}
