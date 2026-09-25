package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户分配角色请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "用户角色分配请求")
@Data
public class UserRoleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色 ID 列表")
    private Set<Long> roleIds;

}
