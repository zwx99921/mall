package com.we.mall.auth.admin.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha 验证码配置
 *
 * @author we
 * @date 2026-09-16
 * @description
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        // 图片边框
        properties.setProperty("kaptcha.border", "no");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "36");
        // 字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        // 字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "023456789ABCDEFGHJKMNOPQRSTUVWXYZ");
        // 干扰线
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 背景渐变
        properties.setProperty("kaptcha.background.clear.from", "255,255,255");
        properties.setProperty("kaptcha.background.clear.to", "255,255,255");
        // 使用自定义渲染器（支持中文）
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
