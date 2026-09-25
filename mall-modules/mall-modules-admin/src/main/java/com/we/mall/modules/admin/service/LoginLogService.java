package com.we.mall.modules.admin.service;


/**
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
public interface LoginLogService {

    void record(Long userId, String username, String loginType,
                Integer status, String msg,
                String loginIp, String userAgent,
                String deviceType, String deviceName);

}
