package com.we.mall.auth.admin.service;

import com.we.mall.auth.admin.model.response.CaptchaResponse;

/**
 * 验证码服务
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface CaptchaService {

    CaptchaResponse generate();

    void verify(String captchaId, String captcha);

}
