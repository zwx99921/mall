package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 新增菜单请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "新增菜单请求")
@Data
public class MenuCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父菜单ID，一级菜单为0")
    private Long parentId = 0L;

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
    private Integer sortOrder = 0;

    @Schema(description = "是否固定标签 0-否 1-是")
    @Min(value = 0, message = "affix 不合法")
    @Max(value = 1, message = "affix 不合法")
    private Integer affix = 0;

    @Schema(description = "显示状态 0-隐藏 1-显示")
    @Min(value = 0, message = "visible 不合法")
    @Max(value = 1, message = "visible 不合法")
    private Integer visible = 1;

    @Schema(description = "状态 0-禁用 1-启用")
    @Min(value = 0, message = "状态不合法")
    @Max(value = 1, message = "状态不合法")
    private Integer status = 1;

}
