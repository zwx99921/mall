package com.we.mall.modules.admin.model.request;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.we.mall.common.mybatis.param.BaseUpdateParam;
import com.we.mall.modules.admin.model.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改用户请求
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "修改用户请求")
public class UserUpdateRequest extends BaseUpdateParam<UserEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称最长 32 个字符")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式错误")
    private String email;

    @Override
    public void buildUpdate(LambdaUpdateWrapper<UserEntity> wrapper) {
        wrapper.set(UserEntity::getNickname, nickname)
                .set(UserEntity::getAvatar, avatar)
                .set(UserEntity::getPhone, phone)
                .set(UserEntity::getEmail, email);
    }
}
