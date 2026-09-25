package com.we.mall.modules.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.we.mall.modules.admin.mapper.LoginLogMapper;
import com.we.mall.modules.admin.model.entity.LoginLogEntity;
import com.we.mall.modules.admin.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLogEntity> implements LoginLogService {

    @Override
    public void record(Long userId, String username, String loginType,
                       Integer status, String msg,
                       String loginIp, String userAgent,
                       String deviceType, String deviceName) {

        LoginLogEntity loginLogEntity = new LoginLogEntity();

        loginLogEntity.setId(userId);
        loginLogEntity.setUsername(username);
        loginLogEntity.setLoginIp(loginIp);
        loginLogEntity.setUserAgent(userAgent);
        loginLogEntity.setDeviceType(deviceType);
        loginLogEntity.setDeviceName(deviceName);
        loginLogEntity.setLoginType(loginType);
        loginLogEntity.setStatus(status);
        loginLogEntity.setMsg(msg);
        loginLogEntity.setLoginTime(LocalDateTime.now());

        this.baseMapper.insert(loginLogEntity);
    }

}
