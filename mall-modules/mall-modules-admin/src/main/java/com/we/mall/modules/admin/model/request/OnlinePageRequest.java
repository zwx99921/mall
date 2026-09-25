package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 在线用户分页请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "在线用户分页请求")
@Data
public class OnlinePageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码，从 1 开始")
    private Long pageNum = 1L;

    @Schema(description = "每页数量")
    private Long pageSize = 10L;

    @Schema(description = "用户名（模糊）")
    private String username;

}