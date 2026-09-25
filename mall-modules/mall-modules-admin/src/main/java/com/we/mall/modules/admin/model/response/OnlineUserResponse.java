package com.we.mall.modules.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 在线用户响应（按 userId 分组）
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "在线用户响应")
@Data
public class OnlineUserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "在线设备数")
    private Integer sessionCount;

    @Schema(description = "设备类型列表")
    private List<String> deviceTypes;

    @Schema(description = "最近活跃时间")
    private LocalDateTime lastAccessTime;

}
