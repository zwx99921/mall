package com.we.mall.modules.file.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 预签名上传请求
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Schema(description = "预签名上传请求")
@Data
public class FilePresignUploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原始文件名")
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @Schema(description = "业务类型")
    private String bizType;

}
