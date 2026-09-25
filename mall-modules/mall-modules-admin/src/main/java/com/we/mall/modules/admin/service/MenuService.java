package com.we.mall.modules.admin.service;

import com.we.mall.modules.admin.model.request.MenuCreateRequest;
import com.we.mall.modules.admin.model.request.MenuUpdateRequest;
import com.we.mall.modules.admin.model.response.MenuResponse;
import com.we.mall.modules.admin.model.response.MenuTreeResponse;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public interface MenuService {

    /**
     * 当前登录用户的路由树（目录 + 菜单，visible=1）
     */
    List<MenuTreeResponse> routes();

    /**
     * 菜单树（全量）
     */
    List<MenuTreeResponse> tree();

    /**
     * 菜单详情
     */
    MenuResponse detail(Long menuId);

    /**
     * 新增菜单
     */
    Long create(MenuCreateRequest request);

    /**
     * 修改菜单
     */
    void update(Long menuId, MenuUpdateRequest request);

    /**
     * 删除菜单
     */
    void delete(Long menuId);

}
