package com.we.mall.common.security.annotation;

import com.we.mall.common.security.enums.Logical;

import java.lang.annotation.*;

/**
 * 要求权限
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /**
     * 需要的权限
     */
    String[] value();

    /**
     * 多个权限间的关系
     */
    Logical logical() default Logical.AND;

}
