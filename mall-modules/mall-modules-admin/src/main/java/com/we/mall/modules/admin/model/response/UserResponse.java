package com.we.mall.modules.admin.model.response;

import com.we.mall.common.sensitive.annotation.Sensitive;
import com.we.mall.common.sensitive.enums.SensitiveSceneType;
import com.we.mall.common.sensitive.enums.SensitiveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "用户响应")
@Data
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机")
    @Sensitive(type = SensitiveType.PHONE, scenes = SensitiveSceneType.PAGE)
    private String phone;

    @Schema(description = "邮箱")
    @Sensitive(type = SensitiveType.EMAIL, scenes = SensitiveSceneType.PAGE)
    private String email;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
