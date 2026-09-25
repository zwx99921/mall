package com.we.mall.common.web.handler;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.*;
import com.we.mall.common.core.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * HTTP status 由异常类型决定：
 * BusinessException     → 200
 * UnauthorizedException → 401
 * ForbiddenException    → 403
 * SystemException       → 500
 * RemoteException       → 502
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 → 200 ====================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R<Void>> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [{}]: code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(200)
                .body(R.fail(e.getResultCode()));
    }

    // ==================== 未认证 → 401 ====================
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<R<Void>> handleUnauthorized(UnauthorizedException e, HttpServletRequest request) {
        log.warn("未认证 [{}]: code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(401)
                .body(R.fail(e.getResultCode()));
    }

    // ==================== 无权限 → 403 ====================
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<R<Void>> handleForbidden(ForbiddenException e, HttpServletRequest request) {
        log.warn("无权限 [{}]: code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(403)
                .body(R.fail(e.getResultCode()));
    }

    // ==================== 系统异常 → 500 ====================
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<R<Void>> handleSystem(SystemException e, HttpServletRequest request) {
        log.error("系统异常 [{}]: code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(500)
                .body(R.fail(e.getResultCode()));
    }

    // ==================== 远程调用 → 502 ====================
    @ExceptionHandler(RemoteException.class)
    public ResponseEntity<R<Void>> handleRemote(RemoteException e, HttpServletRequest request) {
        log.error("远程调用异常 [{}]: service={}, code={}, message={}", request.getRequestURI(), e.getServiceName(), e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(502)
                .body(R.fail(e.getResultCode()));
    }

    // ==================== 参数校验 → 400 ====================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Void>> handleValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数校验失败 [{}]: {}", request.getRequestURI(), msg, e);
        return ResponseEntity
                .status(400)
                .body(R.fail(ResultCode.PARAM_ERROR.getCode(), msg));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Void>> handleBind(BindException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数绑定失败 [{}]: {}", request.getRequestURI(), msg, e);
        return ResponseEntity
                .status(400)
                .body(R.fail(ResultCode.PARAM_ERROR.getCode(), msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R<Void>> handleConstraint(ConstraintViolationException e, HttpServletRequest request) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数约束失败 [{}]: {}", request.getRequestURI(), msg, e);
        return ResponseEntity
                .status(400)
                .body(R.fail(ResultCode.PARAM_ERROR.getCode(), msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<R<Void>> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少参数 [{}]: {}", request.getRequestURI(), e.getParameterName(), e);
        return ResponseEntity
                .status(400)
                .body(R.fail(ResultCode.PARAM_ERROR.getCode(), "缺少参数: " + e.getParameterName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<Void>> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("请求体解析失败 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity
                .status(400)
                .body(R.fail(ResultCode.PARAM_ERROR.getCode(), "请求体格式错误"));
    }

    // ==================== 方法不支持 → 405 ====================
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<R<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity
                .status(405)
                .body(R.fail(ResultCode.METHOD_NOT_ALLOWED));
    }

    // ==================== 兜底异常 → 500 ====================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 [{}]", request.getRequestURI(), e);
        return ResponseEntity
                .status(500)
                .body(R.fail(ResultCode.SERVICE_ERROR));
    }

}
