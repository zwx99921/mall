package com.we.mall.modules.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.we.mall.common.security.util.SecurityUtils;
import com.we.mall.modules.admin.convert.MenuConvert;
import com.we.mall.modules.admin.mapper.MenuMapper;
import com.we.mall.modules.admin.mapper.RoleMenuMapper;
import com.we.mall.modules.admin.mapper.UserRoleMapper;
import com.we.mall.modules.admin.model.entity.MenuEntity;
import com.we.mall.modules.admin.model.request.MenuCreateRequest;
import com.we.mall.modules.admin.model.request.MenuUpdateRequest;
import com.we.mall.modules.admin.model.response.MenuResponse;
import com.we.mall.modules.admin.model.response.MenuTreeResponse;
import com.we.mall.modules.admin.service.MenuService;
import com.we.mall.modules.admin.service.support.SessionRefreshSupport;
import com.we.mall.modules.admin.service.validator.MenuValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    private final MenuConvert menuConvert;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final MenuValidator menuValidator;
    private final SessionRefreshSupport sessionRefreshSupport;

    @Override
    public List<MenuTreeResponse> routes() {
        Long userId = SecurityUtils.requireUserId();
        List<MenuEntity> all = baseMapper.selectRoutesByUserId(userId);
        return buildTree(all);
    }

    @Override
    public List<MenuTreeResponse> tree() {
        List<MenuEntity> all = baseMapper.selectAllOrdered();
        return buildTree(all);
    }

    @Override
    public MenuResponse detail(Long menuId) {
        MenuEntity entity = menuValidator.checkAndGet(menuId);
        return menuConvert.toResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MenuCreateRequest request) {
        menuValidator.checkParentValid(null, request.getParentId());
        MenuEntity entity = menuConvert.toEntity(request);
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long menuId, MenuUpdateRequest request) {
        menuValidator.checkExists(menuId);
        menuValidator.checkParentValid(menuId, request.getParentId());

        baseMapper.update(null, request.toUpdateWrapper().eq(MenuEntity::getId, menuId));

        // 状态或 perms 变了 → 刷新受影响用户
        if (request.getStatus() != null || request.getPerms() != null) {
            List<Long> userIds = userRoleMapper.selectUserIdsByMenuId(menuId);
            sessionRefreshSupport.refresh(userIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long menuId) {
        menuValidator.checkExists(menuId);
        menuValidator.checkCanDelete(menuId);

        // 先查受影响的用户
        List<Long> userIds = userRoleMapper.selectUserIdsByMenuId(menuId);

        // 删菜单 + 删角色菜单关联
        baseMapper.deleteById(menuId);
        roleMenuMapper.deleteByMenuId(menuId);

        // 刷新
        sessionRefreshSupport.refresh(userIds);
    }

    /**
     * 组装菜单树
     */
    private List<MenuTreeResponse> buildTree(List<MenuEntity> all) {
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }

        List<MenuTreeResponse> nodes = all.stream()
                .map(menuConvert::toTreeResponse)
                .collect(Collectors.toList());

        Map<Long, List<MenuTreeResponse>> parentMap = nodes.stream()
                .collect(Collectors.groupingBy(MenuTreeResponse::getParentId));

        for (MenuTreeResponse node : nodes) {
            List<MenuTreeResponse> children = parentMap.get(node.getMenuId());
            if (children != null) {
                node.setChildren(children);
            }
        }

        List<MenuTreeResponse> roots = parentMap.get(0L);
        return roots == null ? Collections.emptyList() : roots;
    }

}
