package com.we.mall.auth.admin.controller;

import com.we.mall.auth.admin.model.response.CaptchaResponse;
import com.we.mall.auth.admin.service.CaptchaService;
import com.we.mall.common.core.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Tag(name = "验证码", description = "验证码接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Validated
public class CaptchaController {

    private final CaptchaService captchaService;

    @Operation(summary = "验证码", description = "生成验证码")
    @GetMapping("/captcha/generate")
    public R<CaptchaResponse> generate() {
        return R.ok(captchaService.generate());
    }

}
