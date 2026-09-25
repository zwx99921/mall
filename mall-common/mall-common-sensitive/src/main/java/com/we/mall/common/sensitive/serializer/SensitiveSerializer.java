package com.we.mall.common.sensitive.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.we.mall.common.sensitive.annotation.Sensitive;
import com.we.mall.common.sensitive.context.SensitiveSceneContext;
import com.we.mall.common.sensitive.enums.SensitiveSceneType;
import com.we.mall.common.sensitive.enums.SensitiveType;
import com.we.mall.common.sensitive.factory.SensitiveStrategyFactory;
import com.we.mall.common.sensitive.strategy.SensitiveStrategy;

import java.io.IOException;
import java.util.Objects;

/**
 * 敏感信息序列化器
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type;

    private SensitiveSceneType[] scenes;

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveType type, SensitiveSceneType[] scenes) {
        this.type = type;
        this.scenes = scenes;
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null || value.isEmpty()) {
            jsonGenerator.writeString(value);
            return;
        }

        // 当前场景不在字段声明的 scenes 里 → 原样输出
        if (!SensitiveSceneContext.match(scenes)) {
            jsonGenerator.writeString(value);
            return;
        }

        SensitiveStrategy strategy = SensitiveStrategyFactory.getStrategy(type);
        jsonGenerator.writeString(strategy.desensitize(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return serializerProvider.findNullValueSerializer(null);
        }
        if (Objects.equals(String.class, beanProperty.getType().getRawClass())) {
            Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
            if (sensitive == null) {
                sensitive = beanProperty.getContextAnnotation(Sensitive.class);
            }
            if (sensitive != null) {
                return new SensitiveSerializer(sensitive.type(), sensitive.scenes());
            }
        }
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}
