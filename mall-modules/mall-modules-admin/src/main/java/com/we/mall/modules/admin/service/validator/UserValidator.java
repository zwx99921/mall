package com.we.mall.modules.admin.service.validator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.security.util.SecurityUtils;
import com.we.mall.modules.admin.mapper.UserMapper;
import com.we.mall.modules.admin.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户校验器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserMapper userMapper;

    /**
     * 校验用户存在
     *
     * @param userId 用户ID
     */
    public void checkExists(Long userId) {
        boolean exists = userMapper.exists(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, userId));
        if (!exists) {
            throw BusinessException.of(ResultCode.ADMIN_NOT_FOUND);
        }
    }

    /**
     * 校验用户存在，并返回
     */
    public UserEntity checkAndGet(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.ADMIN_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 按用户名校验用户存在，并返回
     */
    public UserEntity checkAndGetByUsername(String username) {
        UserEntity entity = userMapper.selectByUsername(username);
        if (entity == null) {
            throw BusinessException.of(ResultCode.ADMIN_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 校验用户名唯一
     *
     * @param username 用户名
     */
    public void checkUsernameUnique(String username) {
        long count = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        if (count > 0) {
            throw BusinessException.of(ResultCode.ADMIN_ALREADY_EXISTS);
        }
    }

    /**
     * 校验可删除（不能删自己）
     *
     * @param userId 用户ID
     */
    public void checkCanDelete(Long userId) {
        Long currentUserId = SecurityUtils.getUserId();
        if (userId.equals(currentUserId)) {
            throw BusinessException.of(ResultCode.CANNOT_DELETE_SELF);
        }
    }

    /**
     * 校验可禁用（不能禁用自己）
     *
     * @param userId 用户ID
     * @param status 目标状态：0 禁用 1 启用
     */
    public void checkCanDisable(Long userId, Integer status) {
        Long currentUserId = SecurityUtils.getUserId();
        if (userId.equals(currentUserId) && Integer.valueOf(0).equals(status)) {
            throw BusinessException.of(ResultCode.CANNOT_DISABLE_SELF);
        }
    }

}
