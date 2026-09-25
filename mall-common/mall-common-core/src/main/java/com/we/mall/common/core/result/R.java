package com.we.mall.common.core.result;

import com.we.mall.common.core.enums.ResultCode;
import lombok.Data;

/**
 * 统一结果返回
 *
 * @author we
 * @date 2026-06-09
 * @description
 */

@Data
public class R<T> {

    /**
     * 请求响应状态
     */
    private Integer code;

    /**
     * 请求响应消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 私有化构造
     */
    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    // =============== ok ===============
    public static <T> R<T> ok() {
        return new R<T>()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage());
    }

    public static <T> R<T> ok(Integer code, String message) {
        return R.<T>ok()
                .code(code)
                .message(message);
    }

    public static <T> R<T> ok(T data) {
        return R.<T>ok()
                .data(data);
    }

    public static <T> R<T> ok(T data, String message) {
        return R.<T>ok()
                .data(data)
                .message(message);
    }

    // =============== fail ===============
    public static <T> R<T> fail() {
        return new R<T>()
                .code(ResultCode.FAIL.getCode())
                .message(ResultCode.FAIL.getMessage());
    }

    public static <T> R<T> fail(String message) {
        return R.<T>fail()
                .message(message);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return R.<T>fail()
                .code(code)
                .message(message);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return R.<T>fail()
                .code(resultCode.getCode())
                .message(resultCode.getMessage());
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return R.<T>fail()
                .code(resultCode.getCode())
                .message(message);
    }

    // =============== 内部链式调用 ===============
    private R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    private R<T> message(String message) {
        this.message = message;
        return this;
    }

    private R<T> data(T data) {
        this.data = data;
        return this;
    }

}
