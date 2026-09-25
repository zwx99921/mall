package com.we.mall.common.core.exception;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 远程调用异常 - HTTP 502
 * <p>
 * Feign / RestTemplate / RPC 调用失败。
 * <p>
 * HTTP status 由 {@code GlobalExceptionHandler} 映射为 <b>502</b>。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Getter
public class RemoteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    /**
     * 远程服务名（用于日志定位）
     */
    private final String serviceName;

    // ==================== 构造器 ====================

    public RemoteException(String serviceName, ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.serviceName = serviceName;
    }

    public RemoteException(String serviceName, ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.serviceName = serviceName;
    }

    public RemoteException(String serviceName, ResultCode resultCode,
                           String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
        this.serviceName = serviceName;
    }

    // ==================== getter ====================

    /**
     * 远程调用失败
     */
    public static RemoteException of(String serviceName, String message) {
        return new RemoteException(serviceName, ResultCode.REMOTE_ERROR, message);
    }

    // ==================== 静态工厂 ====================

    public static RemoteException of(String serviceName, String message, Throwable cause) {
        return new RemoteException(serviceName, ResultCode.REMOTE_ERROR, message, cause);
    }

    /**
     * 远程超时
     */
    public static RemoteException timeout(String serviceName) {
        return new RemoteException(serviceName, ResultCode.REMOTE_TIMEOUT);
    }

    public static RemoteException timeout(String serviceName, String message) {
        return new RemoteException(serviceName, ResultCode.REMOTE_TIMEOUT, message);
    }

    /**
     * 服务不可用
     */
    public static RemoteException unavailable(String serviceName) {
        return new RemoteException(serviceName, ResultCode.REMOTE_SERVICE_UNAVAILABLE);
    }

    /**
     * 通用工厂
     */
    public static RemoteException of(String serviceName, ResultCode resultCode, String message) {
        return new RemoteException(serviceName, resultCode, message);
    }

    public Integer getCode() {
        return resultCode.getCode();
    }


}
