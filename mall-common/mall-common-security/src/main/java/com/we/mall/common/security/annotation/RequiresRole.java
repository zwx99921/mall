package com.we.mall.common.security.annotation;

import com.we.mall.common.security.enums.Logical;

import java.lang.annotation.*;

/**
 * 要求角色
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {

    /**
     * 需要的角色
     */
    String[] value();

    /**
     * 多个角色间的关系
     */
    Logical logical() default Logical.AND;

}
