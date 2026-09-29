package com.we.mall.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 统一业务状态码枚举
 *
 * @author we
 * @date 2026-07-07
 * @description
 */

@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 成功 ====================
    SUCCESS(0, "成功"),
    FAIL(99999, "操作失败"),

    // ==================== 1xxxx 通用 ====================
    // 10xxx 参数
    PARAM_ERROR(10001, "参数错误"),
    PARAM_MISSING(10002, "缺少必要参数"),
    PARAM_FORMAT_ERROR(10003, "参数格式错误"),

    // 11xxx 请求
    METHOD_NOT_ALLOWED(11001, "请求方法不支持"),
    REQUEST_TOO_LARGE(11002, "请求体过大"),
    CONTENT_TYPE_ERROR(11003, "Content-Type 不支持"),
    CLIENT_TYPE_NOT_SUPPORT(11004, "当前设备不支持访问"),

    // 12xxx 资源
    NOT_FOUND(12001, "资源不存在"),
    RESOURCE_CONFLICT(12002, "资源冲突"),

    // 13xxx 认证权限（网关层）
    UNAUTHORIZED(13001, "未登录"),
    FORBIDDEN(13002, "无权限"),

    // 14xxx 限流防重
    REPEAT_SUBMIT(14001, "请勿重复提交"),
    RATE_LIMIT(14002, "请求过于频繁"),


    // ==================== 2xxxx 认证 ====================
    // 20xxx 登录（共用）
    LOGIN_FAILED(20001, "用户名或密码错误"),
    ACCOUNT_DISABLED(20002, "账号已禁用"),
    LOGIN_LOCKED(20003, "账号已锁定，请稍后再试"),
    LOGIN_ATTEMPT_EXCEED(20004, "尝试次数过多，请稍后再试"),

    // 21xxx 验证码（共用）
    CAPTCHA_GENERATE_ERROR(21001, "验证码生成失败"),
    CAPTCHA_EMPTY(21002, "验证码不能为空"),
    CAPTCHA_EXPIRED(21003, "验证码已过期"),
    CAPTCHA_ERROR(21004, "验证码错误"),
    CAPTCHA_SEND_TOO_FREQUENT(21005, "验证码发送过于频繁"),

    // 22xxx Token / Session（共用）
    TOKEN_MISSING(22001, "Token 缺失"),
    TOKEN_INVALID(22002, "Token 无效"),
    TOKEN_EXPIRED(22003, "Token 已过期"),
    REFRESH_TOKEN_INVALID(22004, "RefreshToken 无效"),
    REFRESH_TOKEN_EXPIRED(22005, "RefreshToken 已过期"),
    SESSION_EXPIRED(22006, "会话已过期，请重新登录"),
    SESSION_KICKED(22007, "账号已在其他设备登录"),

    // 23xxx 登出（共用）
    LOGOUT_FAILED(23001, "登出失败"),

    // 24xxx 管理端特有
    ADMIN_PERMISSION_DENIED(24001, "管理员权限不足"),
    ADMIN_ALREADY_EXISTS(24002, "管理员已存在"),
    ADMIN_NOT_FOUND(24003, "管理员不存在"),
    ADMIN_ROLE_DISABLED(24004, "管理员角色已禁用"),
    CANNOT_DELETE_SELF(24005, "不能删除自己"),
    CANNOT_DISABLE_SELF(24006, "不能禁用自己"),
    ROLE_NOT_FOUND(2407, "角色不存在"),
    ROLE_ALREADY_EXISTS(2408, "角色已存在"),
    MENU_NOT_FOUND(2409, "菜单不存在"),
    MENU_HAS_CHILDREN(24010, "存在子菜单，不允许删除"),
    MENU_PARENT_INVALID(24011, "父菜单不能是自己"),
    CANNOT_KICK_SELF(24012, "不能踢自己下线"),
    ONLINE_USER_NOT_FOUND(24013, "用户不在线"),
    ONLINE_SESSION_NOT_FOUND(24014, "会话不存在或已失效"),

    // 25xxx 会员端特有
    MEMBER_PHONE_NOT_REGISTERED(25001, "手机号未注册"),
    MEMBER_PHONE_ALREADY_REGISTERED(25002, "手机号已注册"),
    MEMBER_WECHAT_AUTH_FAILED(25003, "微信授权失败"),

    // ========== 3xxxx 会员（预留） ==========
    USER_NOT_FOUND(30001, "用户不存在"),
    USER_ALREADY_EXISTS(30002, "用户已存在"),
    USER_DISABLED(30003, "用户已禁用"),

    // ========== 4xxxx 文件 ==========
    FILE_EMPTY(40001, "文件为空"),
    FILE_NOT_FOUND(40002, "文件不存在"),
    FILE_SIZE_EXCEED(40003, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(40004, "文件类型不允许"),
    FILE_PATH_INVALID(40005, "文件路径非法"),
    FILE_UPLOAD_ERROR(40006, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(40007, "文件下载失败"),
    FILE_DELETE_ERROR(40008, "文件删除失败"),
    FILE_STORAGE_NOT_SUPPORTED(40009, "不支持的存储类型"),
    FILE_PRESIGN_NOT_SUPPORTED(40010, "当前存储不支持预签名"),

    // ========== 5xxxx （预留） ==========

    // ========== 6xxxx （预留） ==========

    // ========== 7xxxx 远程调用 ==========
    REMOTE_ERROR(70001, "远程服务调用失败"),
    REMOTE_TIMEOUT(70002, "远程服务超时"),
    REMOTE_SERVICE_UNAVAILABLE(70003, "远程服务不可用"),

    // ========== 9xxxx 系统 ==========
    SERVICE_ERROR(90001, "系统繁忙，请稍后再试"),
    DB_ERROR(90002, "数据库异常"),
    CONFIG_ERROR(90003, "配置错误"),
//    FILE_UPLOAD_ERROR(90004, "文件上传失败"),


    // 文件相关
    EXPORT_ERROR(90005, "导出失败"),
    IMPORT_ERROR(90006, "导入失败"),
    IMPORT_FILE_EMPTY(90007, "导入文件为空"),
    IMPORT_FILE_TYPE_ERROR(90008, "文件类型错误"),
    ;

    private final Integer code;
    private final String message;

    /**
     * 根据 code 获取枚举
     */
    public static ResultCode of(Integer code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equals(code))
                .findFirst()
                .orElse(null);
    }

}
