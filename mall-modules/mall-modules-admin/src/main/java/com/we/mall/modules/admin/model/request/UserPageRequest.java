package com.we.mall.modules.admin.model.request;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.we.mall.common.mybatis.param.BaseQueryParam;
import com.we.mall.modules.admin.model.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 用户分页查询请求
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Schema(description = "用户查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageRequest extends BaseQueryParam<UserEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "状态")
    private Integer status;

    @Override
    public void buildQuery(LambdaQueryWrapper<UserEntity> wrapper) {
        wrapper.like(StringUtils.hasText(username), UserEntity::getUsername, username)
                .like(StringUtils.hasText(nickname), UserEntity::getNickname, nickname)
                .eq(status != null, UserEntity::getStatus, status);
    }

    @Override
    protected List<OrderItem> getDefaultOrders() {
        return Collections.unmodifiableList(
                Arrays.asList(
                        OrderItem.asc("id"),
                        OrderItem.desc("create_time")
                )
        );
    }

    @Override
    protected Set<String> getAllowedSortColumns() {
        return new HashSet<>(
                Arrays.asList("create_time", "update_time", "username")
        );
    }
}
