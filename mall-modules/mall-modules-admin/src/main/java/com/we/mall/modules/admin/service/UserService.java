package com.we.mall.modules.admin.service;


import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.common.excel.result.ImportResult;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.modules.admin.model.excel.UserExport;
import com.we.mall.modules.admin.model.request.UserCreateRequest;
import com.we.mall.modules.admin.model.request.UserPageRequest;
import com.we.mall.modules.admin.model.request.UserUpdateRequest;
import com.we.mall.modules.admin.model.response.UserInfoResponse;
import com.we.mall.modules.admin.model.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 *
 * @author we
 * @date 2026-09-13
 * @description
 */
public interface UserService {

    UserInfoResponse info();

    PageResult<UserResponse> page(UserPageRequest request);

    UserResponse detail(Long userId);

    Long create(UserCreateRequest request);

    void update(Long userId, UserUpdateRequest request);

    void delete(Long userId);

    void updateStatus(Long userId, Integer status);

    void updatePassword(Long userId, String password);

    Set<Long> getRoleIds(Long userId);

    void assignRoles(Long userId, Set<Long> roleIds);

    List<UserExport> exports(UserPageRequest request);

    ImportResult imports(MultipartFile file);

    UserDTO loadByUsername(String username);

    UserDTO loadByUserId(Long userId);

}
