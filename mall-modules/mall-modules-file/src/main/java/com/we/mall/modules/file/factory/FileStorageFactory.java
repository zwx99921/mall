package com.we.mall.modules.file.factory;

import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.SystemException;
import com.we.mall.modules.file.properties.FileProperties;
import com.we.mall.modules.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件存储工厂
 * <p>
 * 按配置选实现，启动时把所有 FileStorage 实现注册进来。
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Component
@RequiredArgsConstructor
public class FileStorageFactory {

    private final List<FileStorage> storages;

    private final FileProperties properties;

    /**
     * 按配置拿当前存储
     */
    public FileStorage get() {
        String type = properties.getStorageType();
        for (FileStorage storage : storages) {
            if (storage.type().equalsIgnoreCase(type)) {
                return storage;
            }
        }
        throw SystemException.of(ResultCode.FILE_STORAGE_NOT_SUPPORTED, type);
    }

    /**
     * 按类型拿指定存储
     */
    public FileStorage get(String type) {
        for (FileStorage storage : storages) {
            if (storage.type().equalsIgnoreCase(type)) {
                return storage;
            }
        }
        throw SystemException.of(ResultCode.FILE_STORAGE_NOT_SUPPORTED, type);
    }

}
