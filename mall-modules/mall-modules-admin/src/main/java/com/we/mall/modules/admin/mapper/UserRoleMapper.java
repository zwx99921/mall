package com.we.mall.modules.admin.mapper;

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
public interface UserRoleMapper {

    /**
     * 按用户查关联的角色ID
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 按角色查关联的用户ID
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 按菜单查受影响的用户ID（角色→菜单 两跳）
     */
    List<Long> selectUserIdsByMenuId(@Param("menuId") Long menuId);

    /**
     * 按用户删除关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量新增关联
     */
    int batchInsert(@Param("list") List<UserRoleEntity> list);

}
