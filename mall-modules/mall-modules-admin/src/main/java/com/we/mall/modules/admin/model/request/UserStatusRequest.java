package com.we.mall.modules.admin.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改用户状态请求
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Schema(description = "修改用户状态请求")
@Data
public class UserStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态：0 禁用 1 启用")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status;

}
