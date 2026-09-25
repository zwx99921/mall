package com.we.mall.modules.file.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 预签名响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "预签名响应")
@Data
public class FilePresignResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "存储 key")
    private String fileKey;

    @Schema(description = "预签名 URL")
    private String url;

    @Schema(description = "过期时间（秒）")
    private Long expireSeconds;

}
