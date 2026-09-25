package com.we.mall.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.we.mall.modules.admin.model.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogEntity> {
}
