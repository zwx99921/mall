package com.we.mall.modules.admin.model.request;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.we.mall.common.mybatis.param.BaseUpdateParam;
import com.we.mall.modules.admin.model.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 修改角色请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "修改角色请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleUpdateRequest extends BaseUpdateParam<RoleEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名")
    @NotBlank(message = "角色名不能为空")
    @Size(max = 64, message = "角色名最长 64")
    private String roleName;

    @Schema(description = "描述")
    @Size(max = 255, message = "描述最长 255")
    private String description;

    @Schema(description = "状态：1 启用 0 禁用")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Override
    public void buildUpdate(LambdaUpdateWrapper<RoleEntity> wrapper) {
        wrapper.set(RoleEntity::getRoleName, roleName)
                .set(RoleEntity::getDescription, description)
                .set(RoleEntity::getStatus, status)
                .set(RoleEntity::getSortOrder, sortOrder);
    }
}
