package com.we.mall.modules.admin.service.validator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.modules.admin.mapper.MenuMapper;
import com.we.mall.modules.admin.model.entity.MenuEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 菜单校验器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Component
@RequiredArgsConstructor
public class MenuValidator {

    private final MenuMapper menuMapper;

    /**
     * 校验菜单存在
     *
     * @param menuId 菜单ID
     */
    public void checkExists(Long menuId) {
        boolean exists = menuMapper.exists(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getId, menuId));
        if (!exists) {
            throw BusinessException.of(ResultCode.MENU_NOT_FOUND);
        }
    }

    /**
     * 校验菜单存在，并返回
     */
    public MenuEntity checkAndGet(Long menuId) {
        MenuEntity entity = menuMapper.selectById(menuId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.MENU_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 校验父菜单合法
     *
     * @param menuId   当前菜单ID（新增时为 null）
     * @param parentId 父菜单ID
     */
    public void checkParentValid(Long menuId, Long parentId) {
        // 不能把自己设成自己的父菜单
        if (menuId != null && menuId.equals(parentId)) {
            throw BusinessException.of(ResultCode.MENU_PARENT_INVALID);
        }
        // 父菜单必须存在（非 0）
        if (parentId != null && parentId != 0L) {
            MenuEntity parent = menuMapper.selectById(parentId);
            if (parent == null) {
                throw BusinessException.of(ResultCode.MENU_NOT_FOUND);
            }
        }
    }

    /**
     * 校验可删除
     *
     * @param menuId 菜单ID
     */
    public void checkCanDelete(Long menuId) {
        // 有子菜单不允许删
        long childCount = menuMapper.selectCount(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getParentId, menuId));
        if (childCount > 0) {
            throw BusinessException.of(ResultCode.MENU_HAS_CHILDREN);
        }
    }

}
