package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 新增用户请求
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Schema(description = "新增用户请求")
@Data
public class UserCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度 3-32")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度 6-32")
    private String password;

    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称最长 32")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式错误")
    private String email;

}
