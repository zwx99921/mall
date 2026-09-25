package com.we.mall.common.core.exception;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 系统异常 - HTTP 500
 * <p>
 * 系统级故障：配置错误、初始化失败、DB 异常等。
 * <p>
 * 与 {@link BusinessException} 的区别：
 * <ul>
 *     <li>BusinessException：业务规则拒绝，日志 warn，HTTP 200</li>
 *     <li>SystemException：系统故障，日志 error，HTTP 500</li>
 * </ul>
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    // ==================== 构造器 ====================

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public SystemException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public SystemException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    // ==================== getter ====================

    /**
     * 默认服务错误
     */
    public static SystemException of(String message) {
        return new SystemException(ResultCode.SERVICE_ERROR, message);
    }

    // ==================== 静态工厂 ====================

    public static SystemException of(String message, Throwable cause) {
        return new SystemException(ResultCode.SERVICE_ERROR, message, cause);
    }

    /**
     * 指定结果码
     */
    public static SystemException of(ResultCode resultCode) {
        return new SystemException(resultCode);
    }

    public static SystemException of(ResultCode resultCode, String message) {
        return new SystemException(resultCode, message);
    }

    public static SystemException of(ResultCode resultCode, String message, Throwable cause) {
        return new SystemException(resultCode, message, cause);
    }

    public Integer getCode() {
        return resultCode.getCode();
    }

}
