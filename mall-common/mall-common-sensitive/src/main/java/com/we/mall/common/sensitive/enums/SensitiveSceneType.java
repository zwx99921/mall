package com.we.mall.common.sensitive.enums;

/**
 * 脱敏场景
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public enum SensitiveSceneType {

    /**
     * 所有场景（默认，和原行为一致）
     */
    ALL,

    /**
     * 分页 / 列表
     */
    PAGE,

    /**
     * 详情
     */
    DETAIL,

    /**
     * 导出
     */
    EXPORT

}
