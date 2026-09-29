package com.we.mall.common.security.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 安全上下文
 *
 * @author we
 * @date 2026-09-29
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityContext implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 会话 ====================

    private String sessionId;

    private String clientType;

    private String deviceType;

    // ==================== 用户 ====================

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    // ==================== 授权 ====================

    private Set<String> roles;

    private Set<String> perms;

}
