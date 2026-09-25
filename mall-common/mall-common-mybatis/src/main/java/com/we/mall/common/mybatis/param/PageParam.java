package com.we.mall.common.mybatis.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 基础分页参数实体
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Data
public class PageParam {

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    @Max(value = 9999, message = "页码不能超过9999")
    private Long pageNum = 1L;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 200, message = "每页条数不能超过200")
    private Long pageSize = 10L;

    /**
     * 转换为 MyBatis-Plus 分页对象
     */
    public <T> Page<T> toPage() {
        return new Page<>(pageNum, pageSize);
    }
}
