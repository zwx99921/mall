package com.we.mall.auth.admin.service;

/**
 * 密码服务
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public interface PasswordService {

    /**
     * 验证密码
     */
    void verify(String username, String userPassword, String password);

}
