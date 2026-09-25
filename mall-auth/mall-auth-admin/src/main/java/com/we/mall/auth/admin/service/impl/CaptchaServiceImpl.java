package com.we.mall.auth.admin.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.we.mall.auth.admin.constant.key.AuthRedisKeys;
import com.we.mall.auth.admin.model.response.CaptchaResponse;
import com.we.mall.auth.admin.service.CaptchaService;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final DefaultKaptcha defaultKaptcha;
    private final RedisStringOpsService redisStringOpsService;
    private final RedisKeyOpsService redisKeyOpsService;

    @Override
    public CaptchaResponse generate() {
        String captcha = defaultKaptcha.createText();

        BufferedImage image = defaultKaptcha.createImage(captcha);

        String base64Image;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            base64Image = "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            throw BusinessException.of(ResultCode.CAPTCHA_GENERATE_ERROR);
        }

        String captchaId = UUID.randomUUID().toString().replace("-", "");

        redisStringOpsService.set(AuthRedisKeys.captchaKey(captchaId), captcha, AuthRedisKeys.CAPTCHA_EXPIRE, TimeUnit.SECONDS);

        log.info("生成验证码成功: captchaId: {}, captcha: {}", captchaId, captcha);

        return new CaptchaResponse(captchaId, base64Image, captcha);
    }

    @Override
    public void verify(String captchaId, String captcha) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captcha)) {
            throw BusinessException.of(ResultCode.CAPTCHA_EMPTY);
        }

        String key = AuthRedisKeys.captchaKey(captchaId);
        String cacheCode = redisStringOpsService.get(key, String.class);

        if (cacheCode == null) {
            throw BusinessException.of(ResultCode.CAPTCHA_EXPIRED);
        }

        if (!captcha.equalsIgnoreCase(cacheCode)) {
            throw BusinessException.of(ResultCode.CAPTCHA_ERROR);
        }

        log.info("检验验证码成功: captchaId: {}, captcha: {}", captchaId, captcha);

        redisKeyOpsService.delete(key);
    }
}
