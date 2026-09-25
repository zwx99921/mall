package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}
