package com.we.mall.common.core.exception;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 业务异常 - HTTP 200
 * <p>
 * 业务规则拒绝：密码错误、库存不足、状态非法等。
 * <p>
 * HTTP status 由 {@code GlobalExceptionHandler} 映射为 <b>200</b>。
 * 前端应判断 body 里的 {@code code}。
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 结果码
     */
    private final ResultCode resultCode;

    // ==================== 构造器 ====================

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    // ==================== getter ====================

    /**
     * 用默认 message
     */
    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    // ==================== 静态工厂 ====================

    /**
     * 自定义 message
     */
    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }

    /**
     * 带原因
     */
    public static BusinessException of(ResultCode resultCode, String message, Throwable cause) {
        return new BusinessException(resultCode, message, cause);
    }

    /**
     * 便捷方法：拿业务码
     */
    public Integer getCode() {
        return resultCode.getCode();
    }

}
