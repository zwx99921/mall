package com.we.mall.common.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会话用户信息
 * <p>
 * 业务方可继承扩展字段。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionUser implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 租户ID
     */
    private Long tenantId;

}
