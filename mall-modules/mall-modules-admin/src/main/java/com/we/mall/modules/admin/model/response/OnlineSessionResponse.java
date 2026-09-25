package com.we.mall.modules.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线会话响应（某个用户的某台设备）
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "在线会话响应")
@Data
public class OnlineSessionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "客户端类型")
    private String clientType;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "User-Agent")
    private String userAgent;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

}
