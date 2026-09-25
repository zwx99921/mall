package com.we.mall.modules.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户信息响应
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Schema(description = "用户信息响应")
@Data
public class UserInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色")
    private Set<String> roles;

    @Schema(description = "权限")
    private Set<String> perms;
}
