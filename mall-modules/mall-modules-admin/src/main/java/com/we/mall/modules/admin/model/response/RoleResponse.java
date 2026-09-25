package com.we.mall.modules.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "角色响应")
@Data
public class RoleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色码")
    private String roleCode;

    @Schema(description = "角色名")
    private String roleName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
