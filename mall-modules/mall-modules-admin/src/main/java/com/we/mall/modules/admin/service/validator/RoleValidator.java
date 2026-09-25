package com.we.mall.modules.admin.service.validator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.modules.admin.mapper.RoleMapper;
import com.we.mall.modules.admin.model.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 角色校验器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Component
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleMapper roleMapper;

    /**
     * 校验角色存在
     *
     * @param roleId 角色ID
     */
    public void checkExists(Long roleId) {
        boolean exists = roleMapper.exists(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getId, roleId));
        if (!exists) {
            throw BusinessException.of(ResultCode.ROLE_NOT_FOUND);
        }
    }

    /**
     * 校验角色存在，并返回
     */
    public RoleEntity checkAndGet(Long roleId) {
        RoleEntity entity = roleMapper.selectById(roleId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.ROLE_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 校验角色码唯一
     *
     * @param roleCode 角色码
     */
    public void checkCodeUnique(String roleCode) {
        long count = roleMapper.selectCount(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, roleCode));
        if (count > 0) {
            throw BusinessException.of(ResultCode.ROLE_ALREADY_EXISTS);
        }
    }

}
