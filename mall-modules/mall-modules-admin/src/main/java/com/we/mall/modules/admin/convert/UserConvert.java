package com.we.mall.modules.admin.convert;

import com.we.mall.api.admin.dto.UserDTO;
import com.we.mall.common.core.enums.UserStatus;
import com.we.mall.modules.admin.model.entity.UserEntity;
import com.we.mall.modules.admin.model.excel.UserExport;
import com.we.mall.modules.admin.model.request.UserCreateRequest;
import com.we.mall.modules.admin.model.response.UserInfoResponse;
import com.we.mall.modules.admin.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

/**
 * 用户转换器
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Mapper(componentModel = "spring")
public interface UserConvert {


    /**
     * Entity → DTO
     */
    @Mapping(target = "userId", source = "entity.id")
    @Mapping(target = "status", source = "entity.status", qualifiedByName = "toUserStatus")
    UserDTO toDTO(UserEntity entity, Set<String> roles, Set<String> perms);

    /**
     * Entity → Response
     */
    @Mapping(target = "userId", source = "entity.id")
    UserInfoResponse toInfoResponse(UserEntity entity, Set<String> roles, Set<String> perms);

    /**
     * Entity → Response
     */
    @Mapping(target = "userId", source = "entity.id")
    UserResponse toResponse(UserEntity entity);

    List<UserResponse> toResponseList(List<UserEntity> entities);

    /**
     * Request → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    UserEntity toEntity(UserCreateRequest request);

    /**
     * Entity → Export
     */
    @Mapping(target = "userId", source = "entity.id")
    @Mapping(target = "statusDesc", source = "status", qualifiedByName = "toUserStatusDesc")
    UserExport toExport(UserEntity entity);

    List<UserExport> toExportList(List<UserEntity> entities);

    // ==================== 自定义映射方法 ====================

    /**
     * Integer → UserStatus
     */
    @Named("toUserStatus")
    default UserStatus toUserStatus(Integer code) {
        return UserStatus.of(code);
    }

    /**
     * Integer → UserStatus.desc
     */
    @Named("toUserStatusDesc")
    default String toUserStatusDesc(Integer status) {
        UserStatus e = UserStatus.of(status);
        return e == null ? "未知" : e.getDesc();
    }

}
