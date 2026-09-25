package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.we.mall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_role")
public class RoleEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 备注
     */
    private String description;

    /**
     * 状态: 1 启用 0 禁用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

}
