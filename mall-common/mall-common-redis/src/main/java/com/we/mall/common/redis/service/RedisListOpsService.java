package com.we.mall.common.redis.service;

import java.util.Collection;
import java.util.List;

/**
 * Redis List 类型操作封装接口
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface RedisListOpsService {

    // ==================== 写 ====================

    /**
     * 左侧入队（栈顶）
     *
     * @param key   键
     * @param value 值
     * @return 入队后列表长度
     */
    Long leftPush(String key, Object value);

    /**
     * 批量左侧入队
     *
     * @param key    键
     * @param values 值集合
     * @return 入队后列表长度
     */
    Long leftPushAll(String key, Collection<?> values);

    /**
     * 右侧入队（队尾）
     *
     * @param key   键
     * @param value 值
     * @return 入队后列表长度
     */
    Long rightPush(String key, Object value);

    /**
     * 批量右侧入队
     *
     * @param key    键
     * @param values 值集合
     * @return 入队后列表长度
     */
    Long rightPushAll(String key, Collection<?> values);

    /**
     * 按索引设置值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    void set(String key, long index, Object value);

    // ==================== 读（安全转换） ====================

    /**
     * 获取指定区间 元素并转换为 List<T>
     *
     * @param key   键
     * @param start 起始索引（含）
     * @param end   结束索引（含），-1 表示末尾
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的 List；不存在返回空 List
     */
    <T> List<T> range(String key, long start, long end, Class<T> clazz);

    /**
     * 获取指定索引元素并转换
     *
     * @param key   键
     * @param index 索引
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 转换后的元素；不存在返回 null
     */
    <T> T index(String key, long index, Class<T> clazz);

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 长度
     */
    Long size(String key);

    // ==================== 弹 ====================

    /**
     * 左侧弹出并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 弹出的元素；为空返回 null
     */
    <T> T leftPop(String key, Class<T> clazz);

    /**
     * 右侧弹出并转换
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 弹出的元素；为空返回 null
     */
    <T> T rightPop(String key, Class<T> clazz);

    // ==================== 删 ====================

    /**
     * 删除指定值
     *
     * @param key   键
     * @param count 删除数量：>0 从左往右删 count 个；<0 从右往左删 |count| 个；=0 全删
     * @param value 目标值
     * @return 实际删除数量
     */
    Long remove(String key, long count, Object value);

    /**
     * 修剪列表，只保留 [start, end] 区间
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     */
    void trim(String key, long start, long end);

}
