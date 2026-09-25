package com.we.mall.modules.file.storage.impl;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.SystemException;
import com.we.mall.modules.file.properties.FileProperties;
import com.we.mall.modules.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘存储实现
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileProperties properties;


    @Override
    public String upload(InputStream in, String originalName, long size, String contentType) {
        String ext = extractExt(originalName);
        String dateDir = LocalDate.now().format(DATE_DIR);
        String fileName = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);

        // 相对 key：yyyy/MM/dd/xxx.ext
        String key = dateDir + "/" + fileName;

        Path basePath = Paths.get(properties.getLocal().getBasePath());
        Path target = basePath.resolve(key).normalize();

        // 防目录穿越
        if (!target.startsWith(basePath)) {
            throw SystemException.of(ResultCode.FILE_PATH_INVALID);
        }

        try {
            Files.createDirectories(target.getParent());
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("本地文件写入失败: {}", target, e);
            throw SystemException.of(ResultCode.FILE_UPLOAD_ERROR, e.getMessage());
        }

        return key;
    }

    @Override
    public InputStream download(String key) {
        Path basePath = Paths.get(properties.getLocal().getBasePath());
        Path target = basePath.resolve(key).normalize();

        if (!target.startsWith(basePath)) {
            throw SystemException.of(ResultCode.FILE_PATH_INVALID);
        }
        if (!Files.exists(target)) {
            throw SystemException.of(ResultCode.FILE_NOT_FOUND);
        }

        try {
            return Files.newInputStream(target);
        } catch (IOException e) {
            log.error("本地文件读取失败: {}", target, e);
            throw SystemException.of(ResultCode.FILE_DOWNLOAD_ERROR, e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        Path basePath = Paths.get(properties.getLocal().getBasePath());
        Path target = basePath.resolve(key).normalize();

        if (!target.startsWith(basePath)) {
            throw SystemException.of(ResultCode.FILE_PATH_INVALID);
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.error("本地文件删除失败: {}", target, e);
            throw SystemException.of(ResultCode.FILE_DELETE_ERROR, e.getMessage());
        }
    }

    @Override
    public String presignUpload(String key, long expireSec) {
        throw new UnsupportedOperationException("当前存储不支持预签名上传: " + type());
    }

    @Override
    public String presignDownload(String key, long expireSec) {
        throw new UnsupportedOperationException("当前存储不支持预签名下载: " + type());
    }

    @Override
    public String type() {
        return "local";
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
