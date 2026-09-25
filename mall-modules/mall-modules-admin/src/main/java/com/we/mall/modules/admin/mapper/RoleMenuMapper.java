package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuEntity> {
}
