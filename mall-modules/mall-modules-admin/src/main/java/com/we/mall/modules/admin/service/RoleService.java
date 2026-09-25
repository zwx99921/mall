package com.we.mall.modules.admin.service;

import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.modules.admin.model.request.RoleCreateRequest;
import com.we.mall.modules.admin.model.request.RolePageRequest;
import com.we.mall.modules.admin.model.request.RoleUpdateRequest;
import com.we.mall.modules.admin.model.response.RoleResponse;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public interface RoleService {

    PageResult<RoleResponse> page(RolePageRequest request);

    RoleResponse detail(Long roleId);

    Long create(RoleCreateRequest request);

    void update(Long roleId, RoleUpdateRequest request);

    void delete(Long roleId);

    List<RoleResponse> listAll();

    Set<Long> getMenuIds(Long roleId);

    void assignMenus(Long roleId, Set<Long> menuIds);

}
