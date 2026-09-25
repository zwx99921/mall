package com.we.mall.auth.admin.annotation;

import com.we.mall.auth.admin.enums.LoginType;

import java.lang.annotation.*;

/**
 * 登录日志注解
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLog {

    /**
     * 登录方式
     */
    LoginType type() default LoginType.PASSWORD;

}
