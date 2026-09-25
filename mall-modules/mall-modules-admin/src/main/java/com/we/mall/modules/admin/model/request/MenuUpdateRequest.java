package com.we.mall.modules.admin.model.request;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.we.mall.common.mybatis.param.BaseUpdateParam;
import com.we.mall.modules.admin.model.entity.MenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 修改菜单请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "修改菜单请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuUpdateRequest extends BaseUpdateParam<MenuEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父菜单ID，一级菜单为0")
    private Long parentId;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称最长 50")
    private String menuName;

    @Schema(description = "前端路径")
    @Size(max = 200, message = "路径最长 200")
    private String path;

    @Schema(description = "前端组件")
    @Size(max = 200, message = "组件最长 200")
    private String component;

    @Schema(description = "权限标识")
    @Size(max = 500, message = "权限标识最长 500")
    private String perms;

    @Schema(description = "类型 0-目录 1-菜单 2-按钮")
    @NotNull(message = "菜单类型不能为空")
    @Min(value = 0, message = "菜单类型不合法")
    @Max(value = 2, message = "菜单类型不合法")
    private Integer type;

    @Schema(description = "菜单图标")
    @Size(max = 50, message = "图标最长 50")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否固定标签 0-否 1-是")
    @Min(value = 0, message = "affix 不合法")
    @Max(value = 1, message = "affix 不合法")
    private Integer affix;

    @Schema(description = "显示状态 0-隐藏 1-显示")
    @Min(value = 0, message = "visible 不合法")
    @Max(value = 1, message = "visible 不合法")
    private Integer visible;

    @Schema(description = "状态 0-禁用 1-启用")
    @Min(value = 0, message = "status 不合法")
    @Max(value = 1, message = "status 不合法")
    private Integer status;

    @Override
    public void buildUpdate(LambdaUpdateWrapper<MenuEntity> wrapper) {
        wrapper.set(MenuEntity::getParentId, parentId)
                .set(MenuEntity::getMenuName, menuName)
                .set(MenuEntity::getPath, path)
                .set(MenuEntity::getComponent, component)
                .set(MenuEntity::getPerms, perms)
                .set(MenuEntity::getType, type)
                .set(MenuEntity::getIcon, icon)
                .set(MenuEntity::getSortOrder, sortOrder)
                .set(MenuEntity::getAffix, affix)
                .set(MenuEntity::getVisible, visible)
                .set(MenuEntity::getStatus, status);
    }
}
