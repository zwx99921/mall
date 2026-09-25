package com.we.mall.common.jwt.provider;

import com.we.mall.common.core.enums.UserType;
import com.we.mall.common.jwt.exception.TokenException;
import com.we.mall.common.jwt.service.JwtService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * JwtService 提供者
 * <p>
 * 按 {@link UserType} 获取对应的 JwtService，避免上层到处 if-else。
 * <p>
 * 设计要点：
 * <ul>
 *     <li>使用 {@link EnumMap}，O(1) 查找</li>
 *     <li>构造后只读，线程安全</li>
 *     <li>构造器接收 Map，新增端无需改动本类</li>
 *     <li>未配置的端：{@link #get} 返回 null，{@link #require} 抛 401</li>
 * </ul>
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public class JwtServiceProvider {

    private final Map<UserType, JwtService> serviceMap;

    /**
     * @param services 端 → JwtService 映射；不可为 null
     */
    public JwtServiceProvider(Map<UserType, JwtService> services) {
        Map<UserType, JwtService> map = new EnumMap<>(UserType.class);
        if (services != null) {
            services.forEach((type, service) -> {
                if (type != null && service != null) {
                    map.put(type, service);
                }
            });
        }
        this.serviceMap = Collections.unmodifiableMap(map);
    }

    /**
     * 按用户类型拿 JwtService，不存在返回 null
     */
    public JwtService get(UserType userType) {
        return userType == null ? null : serviceMap.get(userType);
    }

    /**
     * 按用户类型拿 JwtService，不存在抛 401
     *
     * @throws TokenException 该端未配置 JwtService
     */
    public JwtService require(UserType userType) {
        JwtService service = get(userType);
        if (service == null) {
            throw TokenException.invalid("端未配置 JwtService: " + userType);
        }
        return service;
    }

    /**
     * 是否已配置该端
     */
    public boolean contains(UserType userType) {
        return userType != null && serviceMap.containsKey(userType);
    }

    /**
     * 已配置的所有端（不可变视图）
     */
    public Set<UserType> availableTypes() {
        return serviceMap.keySet();
    }

}
