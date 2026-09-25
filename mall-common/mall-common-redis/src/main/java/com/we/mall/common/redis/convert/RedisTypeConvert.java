package com.we.mall.common.redis.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * Redis 值类型安全转换器
 * <p>
 * 所有从 Redis 取出的值都是 Object，本类负责将其安全地转换为调用方期望的类型
 * <p>
 * 设计原则:
 * 1. 绝不使用 (T) 强转，避免运行期 ClassCastException
 * 2. 先 isInstance 判断，命中则直接 cast，避免多余转换
 * 3. 未命中则交给 Jackson 的 convertValue 兜底转换
 * 4. 支持单对象、泛型对象、List、Set、Map 五类场景
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@RequiredArgsConstructor
public class RedisTypeConvert {

    private final ObjectMapper redisObjectMapper;

    /**
     * 单对象转换
     *
     * @param value Redis 中取出的原始值，可能为 null
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return 转换后的对象；value 为 null 时返回 null
     */
    public <T> T convert(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        // 类型匹配，直接返回，省去一次 JSON 转换
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        // 类型不匹配，交给 Jackson 转换
        return redisObjectMapper.convertValue(value, clazz);
    }

    /**
     * 泛型对象转换，例如 Result<User>、Page<Order>
     *
     * @param value   Redis 中取出的原始值
     * @param typeRef 泛型类型引用，如 new TypeReference<Result<User>>() {}
     * @param <T>     目标泛型
     * @return 转换后的对象；value 为 null 时返回 null
     */
    public <T> T convert(Object value, TypeReference<T> typeRef) {
        if (value == null) {
            return null;
        }
        return redisObjectMapper.convertValue(value, typeRef);
    }

    /**
     * 转换为 List<T>
     *
     * @param value       Redis 中取出的原始值
     * @param elementType 集合元素类型
     * @param <T>         元素泛型
     * @return 转换后的 List；value 为 null 时返回空 List（非 null）
     */
    public <T> List<T> convertList(Object value, Class<T> elementType) {
        if (value == null) {
            return Collections.emptyList();
        }
        return redisObjectMapper.convertValue(value,
                redisObjectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }

    /**
     * 转换为 Set<T>，使用 LinkedHashSet 保持插入顺序
     *
     * @param value       Redis 中取出的原始值
     * @param elementType 集合元素类型
     * @param <T>         元素泛型
     * @return 转换后的 Set；value 为 null 时返回空 Set（非 null）
     */
    public <T> Set<T> convertSet(Object value, Class<T> elementType) {
        if (value == null) {
            return Collections.emptySet();
        }
        return redisObjectMapper.convertValue(value,
                redisObjectMapper.getTypeFactory().constructCollectionType(LinkedHashSet.class, elementType));
    }

    /**
     * 转换为 Map<K, V>，使用 LinkedHashMap 保持顺序
     *
     * @param value     Redis 中取出的原始值
     * @param keyType   Map key 类型
     * @param valueType Map value 类型
     * @param <K>       key 泛型
     * @param <V>       value 泛型
     * @return 转换后的 Map；value 为 null 时返回空 Map（非 null）
     */
    public <K, V> Map<K, V> convertMap(Object value, Class<K> keyType, Class<V> valueType) {
        if (value == null) {
            return Collections.emptyMap();
        }
        return redisObjectMapper.convertValue(value,
                redisObjectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, keyType, valueType));
    }

}
