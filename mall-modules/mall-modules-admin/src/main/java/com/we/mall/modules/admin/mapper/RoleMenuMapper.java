package com.we.mall.modules.admin.mapper;

import com.we.mall.modules.admin.model.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper
public interface RoleMenuMapper {

    /**
     * 按角色查关联的菜单ID
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 按角色删除关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 按菜单删除关联
     */
    int deleteByMenuId(@Param("menuId") Long menuId);

    /**
     * 批量新增关联
     */
    int batchInsert(@Param("list") List<RoleMenuEntity> list);

}
