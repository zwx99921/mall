package com.we.mall.modules.file.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件响应
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "文件响应")
@Data
public class FileResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "MIME 类型")
    private String fileType;

    @Schema(description = "扩展名")
    private String fileExt;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "上传人ID")
    private Long uploadUserId;

    @Schema(description = "上传人用户名")
    private String uploadUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
