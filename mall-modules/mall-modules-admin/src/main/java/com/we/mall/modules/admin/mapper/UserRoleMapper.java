package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    /**
     * 查拥有该角色的所有用户ID
     *
     * @param roleId 角色ID
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查拥有该菜单权限的所有用户ID
     * <p>
     * 链路：用户 → 角色 → 菜单
     *
     * @param menuId 菜单ID
     */
    List<Long> selectUserIdsByMenuId(@Param("menuId") Long menuId);

}
