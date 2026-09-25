package com.we.mall.auth.admin.constant.key;

import com.we.mall.common.redis.constant.key.RedisKeys;

/**
 * 认证 Redis Key
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class AuthRedisKeys {

    /**
     * 验证码过期时间 300 秒
     */
    public final static Long CAPTCHA_EXPIRE = 300L;
    /**
     * 最大重试次数
     */
    public final static Integer LOGIN_FAIL_MAX = 5;
    /**
     * 锁定时间
     */
    public final static Long LOGIN_FAIL_EXPIRE = 1800L;
    private final static String AUTH_PREFIX = RedisKeys.PREFIX + RedisKeys.AUTH;
    private final static String CAPTCHA_PREFIX = AUTH_PREFIX + "captcha:";
    /**
     * 登录失败次数
     */
    private final static String LOGIN_FAIL_PREFIX = AUTH_PREFIX + "loginFail:";

    private AuthRedisKeys() {
    }

    public static String captchaKey(String captchaId) {
        return CAPTCHA_PREFIX + captchaId;
    }

    public static String loginFailKey(String username) {
        return LOGIN_FAIL_PREFIX + username;
    }

}
