package com.we.mall.modules.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.we.mall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户
 *
 * @author we
 * @date 2026-09-13
 * @description
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_user")
public class UserEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;
}
