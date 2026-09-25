package com.we.mall.modules.file.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.we.mall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件信息
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_file")
public class FileEntity extends BaseEntity implements Serializable {

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径/key
     */
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * MIME 类型
     */
    private String fileType;

    /**
     * 扩展名
     */
    private String fileExt;

    /**
     * 存储类型：LOCAL / MINIO / OSS
     */
    private String storageType;

    /**
     * 业务类型：avatar/excel/...
     */
    private String bizType;

    /**
     * 上传人ID
     */
    private Long uploadUserId;

    /**
     * 上传人用户名
     */
    private String uploadUserName;

}
