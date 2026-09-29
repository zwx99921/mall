package com.we.mall.auth.admin.aspect;

import com.we.mall.api.admin.client.LoginLogFeignClient;
import com.we.mall.auth.admin.annotation.LoginLog;
import com.we.mall.auth.admin.model.request.LoginRequest;
import com.we.mall.common.web.util.IpUtils;
import com.we.mall.common.web.util.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.HttpHeaders;

/**
 * 登录日志切面
 * <p>
 * 拦截 @LoginLog，成功 / 失败都记日志。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
//@Aspect
//@Component
@RequiredArgsConstructor
public class LoginLogAspect {

    private final LoginLogFeignClient loginLogFeignClient;

    @Around("@annotation(loginLog)")
    public Object around(ProceedingJoinPoint joinPoint, LoginLog loginLog) throws Throwable {
        // 拿请求信息
        String loginIp = IpUtils.getIpAddr();
        String userAgent = ServletUtils.getHeader(HttpHeaders.USER_AGENT);
//        String deviceType = HeaderUtils.resolve(HeaderConstants.HEADER_CLIENT_TYPE, DeviceType.PC.name());
//        String deviceName = HeaderUtils.resolve(HeaderConstants.HEADER_DEVICE_NAME, null);
        String deviceType = "";
        String deviceName = "";

        Long userId = 0L;

        // 从方法参数里提取 username
        String username = "";
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof LoginRequest) {
            username = ((LoginRequest) args[0]).getUsername();
        }

        try {
            Object result = joinPoint.proceed();
            loginLogFeignClient.record(userId, username, loginLog.type().name(), 1, "登录成功", loginIp, userAgent, deviceType, deviceName);
            return result;
        } catch (Throwable e) {
            loginLogFeignClient.record(userId, username, loginLog.type().name(), 0, e.getMessage(), loginIp, userAgent, deviceType, deviceName);
            throw e;
        }
    }

}
