package com.we.mall.auth.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 验证码响应
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Schema(description = "验证码响应")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码标识
     */
    @Schema(description = "验证码标识")
    private String captchaId;

    /**
     * 验证码图片（Base64）
     */
    @Schema(description = "验证码图片")
    private String captchaImage;

    /**
     * 测试用返回验证码
     */
    @Schema(description = "验证码")
    private String captcha;

}
