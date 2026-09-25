package com.we.mall.modules.file.convert;

import com.we.mall.modules.file.model.entity.FileEntity;
import com.we.mall.modules.file.model.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 文件转换器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Mapper(componentModel = "spring")
public interface FileConvert {

    /**
     * Entity → Response
     */
    @Mapping(target = "fileId", source = "entity.id")
    FileResponse toResponse(FileEntity entity);

    List<FileResponse> toResponseList(List<FileEntity> entities);

}
