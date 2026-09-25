package com.we.mall.common.sensitive.annotation;

import com.we.mall.common.sensitive.enums.SensitiveSceneType;

import java.lang.annotation.*;

/**
 * 标记接口的脱敏场景
 * <p>
 * 标在 Controller 方法上，切面会设置到 {@link com.we.mall.common.sensitive.context.SensitiveSceneContext}。
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveScene {

    /**
     * 场景值
     */
    SensitiveSceneType value();

}
