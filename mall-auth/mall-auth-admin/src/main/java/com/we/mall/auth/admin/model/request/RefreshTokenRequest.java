package com.we.mall.auth.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 刷新 Token 请求
 *
 * @author we
 * @date 2026-09-17
 * @description
 */
@Schema(description = "刷新Token请求")
@Data
public class RefreshTokenRequest implements Serializable {

    @Schema(description = "RefreshToken")
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;

}
