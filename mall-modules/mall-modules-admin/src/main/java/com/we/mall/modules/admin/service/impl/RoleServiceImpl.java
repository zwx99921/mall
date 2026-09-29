package com.we.mall.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.modules.admin.convert.RoleConvert;
import com.we.mall.modules.admin.mapper.RoleMapper;
import com.we.mall.modules.admin.mapper.RoleMenuMapper;
import com.we.mall.modules.admin.mapper.UserRoleMapper;
import com.we.mall.modules.admin.model.entity.RoleEntity;
import com.we.mall.modules.admin.model.entity.RoleMenuEntity;
import com.we.mall.modules.admin.model.request.RoleCreateRequest;
import com.we.mall.modules.admin.model.request.RolePageRequest;
import com.we.mall.modules.admin.model.request.RoleUpdateRequest;
import com.we.mall.modules.admin.model.response.RoleResponse;
import com.we.mall.modules.admin.service.RoleService;
import com.we.mall.modules.admin.service.support.SessionRefreshSupport;
import com.we.mall.modules.admin.service.validator.RoleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleConvert roleConvert;
    private final RoleValidator roleValidator;
    private final SessionRefreshSupport sessionRefreshSupport;

    @Override
    public PageResult<RoleResponse> page(RolePageRequest request) {
        Page<RoleEntity> page = baseMapper.selectPage(request.toPage(), request.toQueryWrapper());
        List<RoleResponse> records = roleConvert.toResponseList(page.getRecords());
        return PageResult.of(page, records);
    }

    @Override
    public RoleResponse detail(Long roleId) {
        RoleEntity entity = roleValidator.checkAndGet(roleId);
        return roleConvert.toResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RoleCreateRequest request) {
        roleValidator.checkCodeUnique(request.getRoleCode());
        RoleEntity entity = roleConvert.toEntity(request);
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long roleId, RoleUpdateRequest request) {
        roleValidator.checkExists(roleId);
        baseMapper.update(null, request.toUpdateWrapper().eq(RoleEntity::getId, roleId));

        if (request.getStatus() != null) {
            List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
            sessionRefreshSupport.refresh(userIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long roleId) {
        roleValidator.checkExists(roleId);
        // 先查受影响的用户
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);

        baseMapper.deleteById(roleId);
        roleMenuMapper.deleteByRoleId(roleId);

        // 刷新
        sessionRefreshSupport.refresh(userIds);
    }

    @Override
    public List<RoleResponse> listAll() {
        List<RoleEntity> list = baseMapper.selectList(
                new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getStatus, 1)
                        .orderByAsc(RoleEntity::getSortOrder));
        return roleConvert.toResponseList(list);
    }

    @Override
    public Set<Long> getMenuIds(Long roleId) {
        return new HashSet<>(roleMenuMapper.selectMenuIdsByRoleId(roleId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, Set<Long> menuIds) {
        roleValidator.checkExists(roleId);

        roleMenuMapper.deleteByRoleId(roleId);

        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenuEntity> list = menuIds.stream()
                    .map(menuId -> {
                        RoleMenuEntity entity = new RoleMenuEntity();
                        entity.setRoleId(roleId);
                        entity.setMenuId(menuId);
                        return entity;
                    })
                    .collect(Collectors.toList());
            roleMenuMapper.batchInsert(list);
        }

        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
        sessionRefreshSupport.refresh(userIds);
    }
}
