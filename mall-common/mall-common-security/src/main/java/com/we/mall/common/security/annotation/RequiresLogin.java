package com.we.mall.common.security.annotation;

import java.lang.annotation.*;

/**
 * 要求登录
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresLogin {
}
