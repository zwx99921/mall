package com.we.mall.common.mybatis.param;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

/**
 * 基础更新参数实体
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public abstract class BaseUpdateParam<T> {

    /**
     * 子类实现：设置需要更新的字段（set）
     */
    public abstract void buildUpdate(LambdaUpdateWrapper<T> wrapper);

    /**
     * 创建 UpdateWrapper
     */
    public LambdaUpdateWrapper<T> toUpdateWrapper() {
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
        buildUpdate(wrapper);
        return wrapper;
    }

}
