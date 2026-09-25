package com.we.mall.modules.admin.convert;

import com.we.mall.modules.admin.model.entity.MenuEntity;
import com.we.mall.modules.admin.model.request.MenuCreateRequest;
import com.we.mall.modules.admin.model.response.MenuResponse;
import com.we.mall.modules.admin.model.response.MenuTreeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 菜单转换器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper(componentModel = "spring")
public interface MenuConvert {

    /**
     * Entity → Response
     */
    @Mapping(target = "menuId", source = "entity.id")
    MenuResponse toResponse(MenuEntity entity);

    List<MenuResponse> toResponseList(List<MenuEntity> entities);

    /**
     * Entity → Response
     */
    @Mapping(target = "menuId", source = "entity.id")
    @Mapping(target = "children", ignore = true)
    MenuTreeResponse toTreeResponse(MenuEntity entity);


    /**
     * Request → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    MenuEntity toEntity(MenuCreateRequest request);

}
