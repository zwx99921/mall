package com.we.mall.common.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.we.mall.common.sensitive.enums.SensitiveSceneType;
import com.we.mall.common.sensitive.enums.SensitiveType;
import com.we.mall.common.sensitive.serializer.SensitiveSerializer;

import java.lang.annotation.*;

/**
 * 敏感信息脱敏注解
 * <p>
 * 标在实体字段上，序列化时自动脱敏。
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveType type() default SensitiveType.DEFAULT;

    /**
     * 脱敏场景，默认 ALL（所有场景都脱敏）
     * <p>
     * 例：{@code @Sensitive(type = PHONE, scenes = SensitiveSceneType.PAGE)}
     * 表示只在分页场景脱敏，详情场景返回原值。
     */
    SensitiveSceneType[] scenes() default {SensitiveSceneType.ALL};

}
