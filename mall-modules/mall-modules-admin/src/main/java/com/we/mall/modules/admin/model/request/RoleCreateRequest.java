package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 新增角色请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "新增角色请求")
@Data
public class RoleCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色码")
    @NotBlank(message = "角色码不能为空")
    @Size(max = 64, message = "角色码最长 64")
    private String roleCode;

    @Schema(description = "角色名")
    @NotBlank(message = "角色名不能为空")
    @Size(max = 64, message = "角色名最长 64")
    private String roleName;

    @Schema(description = "描述")
    @Size(max = 255, message = "描述最长 255")
    private String description;

    @Schema(description = "状态：1 启用 0 禁用")
    private Integer status = 1;

    @Schema(description = "排序")
    private Integer sortOrder = 0;

}
