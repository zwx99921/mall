package com.we.mall.common.mybatis.param;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 带排序的基础查询参数实体
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseQueryParam<T> extends PageParam {

    /**
     * 排序列表
     */
    @Valid
    private List<SortParam> sorts = new ArrayList<>();

    /**
     * 子类实现：构建查询条件
     */
    public abstract void buildQuery(LambdaQueryWrapper<T> wrapper);

    /**
     * 获取默认排序（子类重写）
     */
    protected abstract List<OrderItem> getDefaultOrders();

    /**
     * 子类必须实现：返回允许排序的字段白名单（数据库列名）
     */
    protected abstract Set<String> getAllowedSortColumns();

    /**
     * 获取有效的排序 OrderItem 列表
     */
    public List<OrderItem> getOrderItems() {
        Set<String> allowedColumns = getAllowedSortColumns();

        if (sorts == null || sorts.isEmpty()) {
            return getDefaultOrders();
        }

        // 过滤有效的排序字段并转换
        List<OrderItem> orderItems = sorts.stream()
                .filter(sort -> sort != null && sort.isValid(allowedColumns))
                .map(SortParam::toOrderItem)
                .collect(Collectors.toList());
        return orderItems.isEmpty() ? getDefaultOrders() : orderItems;
    }

    /**
     * 创建 QueryWrapper
     */
    public LambdaQueryWrapper<T> toQueryWrapper() {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        buildQuery(wrapper);
        return wrapper;
    }

    /**
     * 转换为 MyBatis-Plus Page 对象（含排序）
     */
    public <P> Page<P> toPage() {
        Page<P> page = super.toPage();
        List<OrderItem> orderItems = getOrderItems();
        if (!orderItems.isEmpty()) {
            page.addOrder(orderItems);
        }
        return page;
    }

}
