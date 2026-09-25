package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.we.mall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_menu")
public class MenuEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID，一级菜单为0
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 前端路径
     */
    private String path;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 类型 0-目录 1-菜单 2-按钮
     */
    private Integer type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否固定标签 0-否 1-是
     */
    private Integer affix;

    /**
     * 显示状态 0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

}
