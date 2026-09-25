package com.we.mall.auth.admin.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Token响应
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Schema(description = "Token响应")
@Data
public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * access token
     */
    @Schema(description = "AccessToken")
    private String accessToken;

    /**
     * refresh token
     */
    @Schema(description = "RefreshToken")
    private String refreshToken;

    /**
     * 过期时间（毫秒）
     */
    @Schema(description = "过期时间")
    private Long expiresIn;

    private TokenResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }

}
