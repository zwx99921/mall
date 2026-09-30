package com.we.mall.common.monitor.util;

import com.we.mall.common.monitor.enums.FileSizeUnit;

/**
 * 文件大小格式化
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public final class FileSizeUtils {

    private FileSizeUtils() {
    }

    /**
     * 格式化字节
     */
    public static String format(long bytes) {
        if (bytes < FileSizeUnit.KB.getBytes()) {
            return bytes + " " + FileSizeUnit.B.getCode();
        }

        FileSizeUnit[] units = {
                FileSizeUnit.TB, FileSizeUnit.GB,
                FileSizeUnit.MB, FileSizeUnit.KB
        };

        for (FileSizeUnit unit : units) {
            if (bytes >= unit.getBytes()) {
                double size = (double) bytes / unit.getBytes();
                return String.format("%.2f %s", size, unit.getCode());
            }
        }

        return bytes + " " + FileSizeUnit.B.getCode();
    }

    /**
     * 转指定单位
     */
    public static double convert(long bytes, FileSizeUnit unit) {
        return (double) bytes / unit.getBytes();
    }

}
