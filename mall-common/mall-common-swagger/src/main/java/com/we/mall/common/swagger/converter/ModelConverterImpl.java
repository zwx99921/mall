package com.we.mall.common.swagger.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.we.mall.common.core.result.R;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * R<T> schema 名优化
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
public class ModelConverterImpl implements ModelConverter {

    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {
        JavaType javaType = Json.mapper().constructType(annotatedType.getType());
        if (javaType != null && javaType.isTypeOrSubTypeOf(R.class)) {

            Schema<?> schema = new ObjectSchema();

            // 名字：R<TokenResponse>
            JavaType dataType = javaType.containedType(0);
            String dataTypeName = dataType != null ? getTypeName(dataType) : "Object";
            schema.setName("R<" + dataTypeName + ">");

            // 字段
            schema.addProperty("code", new IntegerSchema());
            schema.addProperty("message", new StringSchema());
            if (dataType != null) {
                schema.addProperty("data", modelConverterContext.resolve(
                        new AnnotatedType(dataType).resolveAsRef(true)));
            } else {
                schema.addProperty("data", new ObjectSchema());
            }

            return schema;
        }

        return iterator.next().resolve(annotatedType, modelConverterContext, iterator);
    }

    /**
     * 递归获取泛型名
     */
    private String getTypeName(JavaType javaType) {
        if (javaType.containedTypeCount() > 0) {
            String base = javaType.getRawClass().getSimpleName();
            String args = javaType.getBindings().getTypeParameters().stream()
                    .map(this::getTypeName)
                    .collect(Collectors.joining(", "));
            return base + "<" + args + ">";
        }
        return javaType.getRawClass().getSimpleName();
    }
}
