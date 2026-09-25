package com.we.mall.modules.file.model.request;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.we.mall.common.mybatis.param.BaseQueryParam;
import com.we.mall.modules.file.model.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 文件分页请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "文件分页请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class FilePageRequest extends BaseQueryParam<FileEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原始文件名（模糊）")
    private String fileName;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "上传人ID")
    private Long uploadUserId;

    @Override
    public void buildQuery(LambdaQueryWrapper<FileEntity> wrapper) {
        wrapper.like(StringUtils.hasText(fileName), FileEntity::getFileName, fileName)
                .eq(StringUtils.hasText(bizType), FileEntity::getBizType, bizType)
                .eq(uploadUserId != null, FileEntity::getUploadUserId, uploadUserId);
    }

    @Override
    protected List<OrderItem> getDefaultOrders() {
        return Collections.singletonList(OrderItem.desc("create_time"));
    }

    @Override
    protected Set<String> getAllowedSortColumns() {
        return new HashSet<>(Arrays.asList("create_time", "file_size", "file_name"));
    }
}
