package com.we.mall.api.admin.dto;

import com.we.mall.common.core.enums.UserStatus;
import lombok.Data;

import java.util.Set;

/**
 * 管理员信息
 *
 * @author we
 * @date 2026-09-18
 * @description
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long userId;
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
     * 角色
     */
    private Set<String> roles;
    /**
     * 权限
     */
    private Set<String> perms;
    /**
     * 状态
     */
    private UserStatus status;
}
