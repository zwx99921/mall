package com.we.mall.modules.file.service;

import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.modules.file.model.request.FilePageRequest;
import com.we.mall.modules.file.model.request.FilePresignUploadRequest;
import com.we.mall.modules.file.model.response.FilePresignResponse;
import com.we.mall.modules.file.model.response.FileResponse;
import com.we.mall.modules.file.model.response.FileUploadResponse;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件服务接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public interface FileService {

    /**
     * 上传文件
     */
    FileUploadResponse upload(MultipartFile file, String bizType);

    /**
     * 下载文件
     *
     * @return 输入流 + 元数据
     */
    FileDownload download(Long fileId);

    /**
     * 删除文件
     */
    void delete(Long fileId);

    /**
     * 文件分页
     */
    PageResult<FileResponse> page(FilePageRequest request);

    /**
     * 获取上传预签名
     */
    FilePresignResponse presignUpload(FilePresignUploadRequest request);

    /**
     * 获取下载预签名
     */
    FilePresignResponse presignDownload(Long fileId);


    /**
     * 下载结果包装
     */
    @Getter
    class FileDownload {
        private final InputStream inputStream;
        private final String fileName;
        private final String contentType;
        private final long size;

        public FileDownload(InputStream inputStream, String fileName, String contentType, long size) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
            this.size = size;
        }

    }

}
