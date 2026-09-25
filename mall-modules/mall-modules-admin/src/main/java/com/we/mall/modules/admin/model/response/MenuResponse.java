package com.we.mall.modules.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "菜单响应")
@Data
public class MenuResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "前端路径")
    private String path;

    @Schema(description = "前端组件")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "类型 0-目录 1-菜单 2-按钮")
    private Integer type;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否固定标签 0-否 1-是")
    private Integer affix;

    @Schema(description = "显示状态 0-隐藏 1-显示")
    private Integer visible;

    @Schema(description = "状态 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
