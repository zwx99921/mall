package com.we.mall.auth.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * B端登录请求
 *
 * @author we
 * @date 2026-09-17
 * @description
 */
@Schema(description = "用户登录请求")
@Data
public class LoginRequest implements Serializable {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @Schema(description = "验证码标识")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;

}
