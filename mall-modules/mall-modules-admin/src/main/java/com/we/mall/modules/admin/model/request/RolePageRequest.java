package com.we.mall.modules.admin.model.request;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.we.mall.common.mybatis.param.BaseQueryParam;
import com.we.mall.modules.admin.model.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 角色分页请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "角色分页请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageRequest extends BaseQueryParam<RoleEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色码")
    private String roleCode;

    @Schema(description = "角色名")
    private String roleName;

    @Schema(description = "状态")
    private Integer status;

    @Override
    public void buildQuery(LambdaQueryWrapper<RoleEntity> wrapper) {
        wrapper.like(StringUtils.hasText(roleCode), RoleEntity::getRoleCode, roleCode);
        wrapper.like(StringUtils.hasText(roleName), RoleEntity::getRoleName, roleName);
        wrapper.eq(status != null, RoleEntity::getStatus, status);
    }

    @Override
    protected List<OrderItem> getDefaultOrders() {
        return Collections.singletonList(OrderItem.asc("sort_order"));
    }

    @Override
    protected Set<String> getAllowedSortColumns() {
        return new HashSet<>(Arrays.asList("create_time", "sort_order", "role_code"));
    }
}
