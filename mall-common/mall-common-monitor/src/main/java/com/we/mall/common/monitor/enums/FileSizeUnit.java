package com.we.mall.common.monitor.enums;

import lombok.Getter;

/**
 * 文件大小单位
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Getter
public enum FileSizeUnit {

    B("B", 1L),
    KB("KB", 1024L),
    MB("MB", 1024L * 1024),
    GB("GB", 1024L * 1024 * 1024),
    TB("TB", 1024L * 1024 * 1024 * 1024);

    private final String code;
    private final long bytes;

    FileSizeUnit(String code, long bytes) {
        this.code = code;
        this.bytes = bytes;
    }

}
