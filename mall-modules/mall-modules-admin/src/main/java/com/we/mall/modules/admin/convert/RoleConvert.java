package com.we.mall.modules.admin.convert;

import com.we.mall.modules.admin.model.entity.RoleEntity;
import com.we.mall.modules.admin.model.request.RoleCreateRequest;
import com.we.mall.modules.admin.model.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色转换器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper(componentModel = "spring")
public interface RoleConvert {

    /**
     * Entity → Response
     */
    @Mapping(target = "roleId", source = "entity.id")
    RoleResponse toResponse(RoleEntity entity);

    List<RoleResponse> toResponseList(List<RoleEntity> entities);

    /**
     * Request → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    RoleEntity toEntity(RoleCreateRequest request);
}
