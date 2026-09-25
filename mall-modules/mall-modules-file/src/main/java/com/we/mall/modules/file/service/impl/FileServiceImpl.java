package com.we.mall.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import com.we.mall.common.core.exception.SystemException;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.redis.service.RedisStringOpsService;
import com.we.mall.common.security.util.SecurityUtils;
import com.we.mall.modules.file.convert.FileConvert;
import com.we.mall.modules.file.factory.FileStorageFactory;
import com.we.mall.modules.file.mapper.FileMapper;
import com.we.mall.modules.file.model.entity.FileEntity;
import com.we.mall.modules.file.model.request.FilePageRequest;
import com.we.mall.modules.file.model.request.FilePresignUploadRequest;
import com.we.mall.modules.file.model.response.FilePresignResponse;
import com.we.mall.modules.file.model.response.FileResponse;
import com.we.mall.modules.file.model.response.FileUploadResponse;
import com.we.mall.modules.file.properties.FileProperties;
import com.we.mall.modules.file.service.FileService;
import com.we.mall.modules.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {
    private static final String PRESIGN_USED_PREFIX = "mall:file:presign:used:";

    private final FileStorageFactory storageFactory;
    private final FileProperties properties;
    private final FileConvert fileConvert;
    private final RedisStringOpsService redisStringOpsService;

    // ==================== 上传 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse upload(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.of(ResultCode.FILE_EMPTY);
        }

        // 1. 大小校验
        if (file.getSize() > properties.getMaxSize()) {
            throw BusinessException.of(ResultCode.FILE_SIZE_EXCEED);
        }

        // 2. 扩展名校验
        String originalName = file.getOriginalFilename();
        String ext = extractExt(originalName);
        if (properties.getAllowedExtensions() != null
                && properties.getAllowedExtensions().length > 0) {
            boolean allowed = Arrays.stream(properties.getAllowedExtensions())
                    .anyMatch(e -> e.equalsIgnoreCase(ext));
            if (!allowed) {
                throw BusinessException.of(ResultCode.FILE_TYPE_NOT_ALLOWED);
            }
        }

        // 3. 存储
        FileStorage storage = storageFactory.get();
        String key;
        try (InputStream in = file.getInputStream()) {
            key = storage.upload(in, originalName, file.getSize(), file.getContentType());
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw SystemException.of(ResultCode.FILE_UPLOAD_ERROR, e.getMessage());
        }

        // 4. 元数据入库
        FileEntity entity = new FileEntity();
        entity.setFileName(originalName);
        entity.setFileKey(key);
        entity.setFileSize(file.getSize());
        entity.setFileType(file.getContentType());
        entity.setFileExt(ext);
        entity.setStorageType(storage.type().toUpperCase());
        entity.setBizType(bizType);
        entity.setUploadUserId(SecurityUtils.getUserId());
        entity.setUploadUserName(SecurityUtils.getUsername());
        baseMapper.insert(entity);

        // 5. 返回
        FileUploadResponse response = new FileUploadResponse();
        response.setFileId(entity.getId());
        response.setFileName(originalName);
        response.setFileSize(file.getSize());
        response.setUrl(buildAccessUrl(entity));
        return response;
    }

    // ==================== 下载 ====================

    @Override
    public FileDownload download(Long fileId) {
        FileEntity entity = baseMapper.selectById(fileId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.FILE_NOT_FOUND);
        }
        FileStorage storage = storageFactory.get(entity.getStorageType().toLowerCase());
        InputStream in = storage.download(entity.getFileKey());
        return new FileDownload(in, entity.getFileName(),
                entity.getFileType(), entity.getFileSize() == null ? 0 : entity.getFileSize());
    }

    // ==================== 删除 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long fileId) {
        FileEntity entity = baseMapper.selectById(fileId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.FILE_NOT_FOUND);
        }
        FileStorage storage = storageFactory.get(entity.getStorageType().toLowerCase());
        storage.delete(entity.getFileKey());
        baseMapper.deleteById(fileId);
    }

    // ==================== 分页 ====================

    @Override
    public PageResult<FileResponse> page(FilePageRequest request) {
        Page<FileEntity> page = baseMapper.selectPage(request.toPage(), request.toQueryWrapper());
        List<FileResponse> records = fileConvert.toResponseList(page.getRecords());
        return PageResult.of(page, records);
    }

    // ==================== 预签名 ====================

    @Override
    public FilePresignResponse presignUpload(FilePresignUploadRequest request) {
        FileStorage storage = storageFactory.get();

        String ext = extractExt(request.getFileName());
        String key = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);

        long expire = properties.getPresign().getUploadExpireSeconds();
        String url = storage.presignUpload(key, expire);

        FilePresignResponse response = new FilePresignResponse();
        response.setFileKey(key);
        response.setUrl(url);
        response.setExpireSeconds(expire);
        return response;
    }

    @Override
    public FilePresignResponse presignDownload(Long fileId) {
        FileEntity entity = baseMapper.selectById(fileId);
        if (entity == null) {
            throw BusinessException.of(ResultCode.FILE_NOT_FOUND);
        }
        FileStorage storage = storageFactory.get(entity.getStorageType().toLowerCase());

        long expire = properties.getPresign().getDownloadExpireSeconds();
        String url = storage.presignDownload(entity.getFileKey(), expire);

        FilePresignResponse response = new FilePresignResponse();
        response.setFileKey(entity.getFileKey());
        response.setUrl(url);
        response.setExpireSeconds(expire);
        return response;
    }

    // ==================== 私有辅助 ====================

    private String buildAccessUrl(FileEntity entity) {
        // 本地：urlPrefix + / + fileKey
        String prefix = properties.getLocal().getUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            return entity.getFileKey();
        }
        return prefix.endsWith("/")
                ? prefix + entity.getFileKey()
                : prefix + "/" + entity.getFileKey();
    }

    private String extractExt(String originalName) {
        if (originalName == null) {
            return "";
        }
        int idx = originalName.lastIndexOf('.');
        if (idx < 0 || idx == originalName.length() - 1) {
            return "";
        }
        return originalName.substring(idx + 1).toLowerCase();
    }
}
