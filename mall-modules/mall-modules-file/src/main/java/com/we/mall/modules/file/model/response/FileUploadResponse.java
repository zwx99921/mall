package com.we.mall.modules.file.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "文件上传响应")
@Data
public class FileUploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "访问 URL")
    private String url;

}
