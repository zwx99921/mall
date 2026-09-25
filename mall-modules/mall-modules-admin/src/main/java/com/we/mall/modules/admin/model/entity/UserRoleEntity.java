package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Data
@TableName("tbl_user_role")
public class UserRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}
