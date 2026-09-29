package com.we.mall.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.excel.result.ImportResult;
import com.we.mall.common.excel.util.ExcelUtils;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.security.util.SecurityUtils;
import com.we.mall.common.session.service.SessionService;
import com.we.mall.modules.admin.convert.UserConvert;
import com.we.mall.modules.admin.mapper.MenuMapper;
import com.we.mall.modules.admin.mapper.RoleMapper;
import com.we.mall.modules.admin.mapper.UserMapper;
import com.we.mall.modules.admin.mapper.UserRoleMapper;
import com.we.mall.modules.admin.model.entity.UserEntity;
import com.we.mall.modules.admin.model.entity.UserRoleEntity;
import com.we.mall.modules.admin.model.excel.UserExport;
import com.we.mall.modules.admin.model.excel.UserImport;
import com.we.mall.modules.admin.model.request.UserCreateRequest;
import com.we.mall.modules.admin.model.request.UserPageRequest;
import com.we.mall.modules.admin.model.request.UserUpdateRequest;
import com.we.mall.modules.admin.model.response.UserInfoResponse;
import com.we.mall.modules.admin.model.response.UserResponse;
import com.we.mall.modules.admin.service.UserService;
import com.we.mall.modules.admin.service.support.SessionRefreshSupport;
import com.we.mall.modules.admin.service.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private static final ClientType CLIENT = ClientType.ADMIN;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final UserConvert userConvert;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final UserValidator userValidator;
    private final SessionRefreshSupport sessionRefreshSupport;

    @Override
    public UserInfoResponse info() {
        Long currentUserId = SecurityUtils.requireUserId();
        UserEntity userEntity = userValidator.checkAndGet(currentUserId);
        // 3. 角色 / 权限（从 session，不查 DB）
        Set<String> roles = SecurityUtils.getRoles();
        Set<String> perms = SecurityUtils.getPerms();
        return userConvert.toInfoResponse(userEntity, roles, perms);
    }

    @Override
    public PageResult<UserResponse> page(UserPageRequest request) {
        Page<UserEntity> page = baseMapper.selectPage(request.toPage(), request.toQueryWrapper());
        List<UserResponse> records = userConvert.toResponseList(page.getRecords());
        return PageResult.of(page, records);
    }

    @Override
    public UserResponse detail(Long userId) {
        UserEntity userEntity = userValidator.checkAndGet(userId);
        return userConvert.toResponse(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserCreateRequest request) {
        userValidator.checkUsernameUnique(request.getUsername());

        UserEntity entity = userConvert.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, UserUpdateRequest request) {
        userValidator.checkExists(userId);
        baseMapper.update(null, request.toUpdateWrapper().eq(UserEntity::getId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId) {
        userValidator.checkExists(userId);
        userValidator.checkCanDelete(userId);
        baseMapper.deleteById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Integer status) {
        userValidator.checkExists(userId);
        userValidator.checkCanDisable(userId, status);

        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<UserEntity>()
                .eq(UserEntity::getId, userId)
                .set(UserEntity::getStatus, status);
        baseMapper.update(null, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String password) {
        userValidator.checkExists(userId);
        String encoded = passwordEncoder.encode(password);
        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<UserEntity>()
                .eq(UserEntity::getId, userId)
                .set(UserEntity::getPassword, encoded);
        baseMapper.update(null, wrapper);
        sessionService.kickAll(CLIENT, userId);
    }

    @Override
    public Set<Long> getRoleIds(Long userId) {
        userValidator.checkExists(userId);
        return new HashSet<>(userRoleMapper.selectRoleIdsByUserId(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, Set<Long> roleIds) {
        userValidator.checkExists(userId);

        // 删旧的
        userRoleMapper.deleteByUserId(userId);
        // 加新的
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRoleEntity> list = roleIds.stream()
                    .map(roleId -> {
                        UserRoleEntity entity = new UserRoleEntity();
                        entity.setUserId(userId);
                        entity.setRoleId(roleId);
                        return entity;
                    })
                    .collect(Collectors.toList());
            userRoleMapper.batchInsert(list);
        }

        sessionRefreshSupport.refresh(userId);
    }

    @Override
    public List<UserExport> exports(UserPageRequest request) {
        List<UserEntity> list = baseMapper.selectList(request.toQueryWrapper());
        return userConvert.toExportList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult imports(MultipartFile file) {
        List<UserImport> list = ExcelUtils.read(file, UserImport.class);
        ImportResult result = new ImportResult();
        result.setTotal(list.size());
        result.setSuccess(0);
        result.setFail(0);

        for (int i = 0; i < list.size(); i++) {
            UserImport vo = list.get(i);
            int rowNum = i + 2;   // Excel 行号（含表头）
            try {
                if (!StringUtils.hasText(vo.getUsername())) {
                    throw new IllegalArgumentException("用户名不能为空");
                }
                if (!StringUtils.hasText(vo.getPassword())) {
                    throw new IllegalArgumentException("密码不能为空");
                }

                // 唯一性
                Long count = baseMapper.selectCount(
                        new LambdaQueryWrapper<UserEntity>()
                                .eq(UserEntity::getUsername, vo.getUsername()));
                if (count > 0) {
                    throw new IllegalArgumentException("用户名已存在");
                }

                // 入库
                UserEntity entity = new UserEntity();
                entity.setUsername(vo.getUsername());
                entity.setPassword(passwordEncoder.encode(vo.getPassword()));
                entity.setNickname(vo.getNickname());
                entity.setPhone(vo.getPhone());
                entity.setEmail(vo.getEmail());
                entity.setStatus(1);
                baseMapper.insert(entity);

                result.setSuccess(result.getSuccess() + 1);
            } catch (Exception e) {
                result.setFail(result.getFail() + 1);
                result.getFailReasons().add("第 " + rowNum + " 行: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public UserDTO loadByUsername(String username) {
        UserEntity userEntity = userValidator.checkAndGetByUsername(username);
        Set<String> roles = roleMapper.selectRoleCodesByUserId(userEntity.getId());
        Set<String> perms = menuMapper.selectPermsByUserId(userEntity.getId());
        return userConvert.toDTO(userEntity, roles, perms);
    }

    @Override
    public UserDTO loadByUserId(Long userId) {
        UserEntity userEntity = userValidator.checkAndGet(userId);
        Set<String> roles = roleMapper.selectRoleCodesByUserId(userEntity.getId());
        Set<String> perms = menuMapper.selectPermsByUserId(userEntity.getId());
        return userConvert.toDTO(userEntity, roles, perms);
    }

}
