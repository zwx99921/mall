package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改用户密码请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "修改用户密码请求")
@Data
public class UserPasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "新密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度 6-32")
    private String password;

}
