package com.we.mall.auth.admin.service.impl;

import com.we.mall.auth.admin.constant.key.AuthRedisKeys;
import com.we.mall.auth.admin.service.PasswordService;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.redis.service.RedisKeyOpsService;
import com.we.mall.common.redis.service.RedisStringOpsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 密码服务实现
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final RedisStringOpsService redisStringOpsService;
    private final RedisKeyOpsService redisKeyOpsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void verify(String username, String userPassword, String password) {
        String failKey = AuthRedisKeys.loginFailKey(username);

        Integer failCount = redisStringOpsService.get(failKey, Integer.class);

        if (failCount != null && failCount >= AuthRedisKeys.LOGIN_FAIL_MAX) {
            log.warn("登录失败-账号已锁定: username={}, failCount={}", username, failCount);
            throw BusinessException.of(ResultCode.LOGIN_LOCKED);
        }

        if (!passwordEncoder.matches(password, userPassword)) {
            Long count = redisStringOpsService.increment(failKey, 1);
            redisKeyOpsService.expire(failKey, AuthRedisKeys.LOGIN_FAIL_EXPIRE, TimeUnit.SECONDS);

            log.warn("登录失败-密码错误: username={}, failCount={}", username, count);
            throw BusinessException.of(ResultCode.LOGIN_FAILED);
        }

        // 删除重试次数
        redisKeyOpsService.delete(failKey);
    }
}
