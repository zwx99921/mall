package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Data
@TableName("tbl_role_menu")
public class RoleMenuEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

}
