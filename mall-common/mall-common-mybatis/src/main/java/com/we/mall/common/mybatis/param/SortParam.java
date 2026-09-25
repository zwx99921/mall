package com.we.mall.common.mybatis.param;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.we.mall.common.mybatis.enums.SortDirection;
import lombok.Data;

/**
 * 基础排序参数实体类
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Data
public class SortParam {

    //@NotBlank(message = "排序字段不能为空")
    private String column;

    /**
     * 排序方向
     */
    private SortDirection direction = SortDirection.DESC;

    /**
     * 创建升序排序
     */
    public static SortParam asc(String column) {
        SortParam param = new SortParam();
        param.setColumn(column);
        param.setDirection(SortDirection.ASC);
        return param;
    }

    /**
     * 创建降序排序
     */
    public static SortParam desc(String column) {
        SortParam param = new SortParam();
        param.setColumn(column);
        param.setDirection(SortDirection.DESC);
        return param;
    }

    /**
     * 转换为 MyBatis-Plus OrderItem
     */
    public OrderItem toOrderItem() {
        return direction.isAsc() ? OrderItem.asc(column) : OrderItem.desc(column);
    }

    /**
     * 校验排序字段是否在白名单中
     */
    public boolean isValid(java.util.Set<String> allowedColumns) {
        return column != null && allowedColumns.contains(column);
    }

}
