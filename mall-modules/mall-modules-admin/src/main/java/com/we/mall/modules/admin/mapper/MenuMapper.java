package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单 Mapper
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    /**
     * 查询用户的权限集合
     */
    Set<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 查用户有权限的路由菜单（目录 + 菜单，visible=1）
     */
    List<MenuEntity> selectRoutesByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单（按排序）
     */
    List<MenuEntity> selectAllOrdered();

}
