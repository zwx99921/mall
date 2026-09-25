package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 角色 Mapper
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 查询用户的角色 code 集合
     */
    Set<String> selectRoleCodesByUserId(@Param("userId") Long userId);

}
