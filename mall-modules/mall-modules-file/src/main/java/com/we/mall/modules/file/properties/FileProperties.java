package com.we.mall.modules.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件服务配置
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.file")
public class FileProperties {

    /**
     * 存储类型：local / minio / oss
     */
    private String storageType = "local";

    /**
     * 最大文件大小（字节），默认 10MB
     */
    private long maxSize = 10 * 1024 * 1024L;

    /**
     * 允许的扩展名白名单，为空表示不限制
     */
    private String[] allowedExtensions = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "csv", "zip", "rar"
    };

    /**
     * 本地存储配置
     */
    private Local local = new Local();

    /**
     * 预签名配置
     */
    private Presign presign = new Presign();

    @Data
    public static class Local {

        /**
         * 本地存储根目录
         */
        private String basePath = "/data/mall/file";

        /**
         * 访问 URL 前缀
         */
        private String urlPrefix = "http://localhost:9101/file";
    }

    @Data
    public static class Presign {

        /**
         * 签名密钥（独立于其他密钥）
         */
        private String secret = "change-me-file-presign-secret";

        /**
         * 上传预签名默认过期秒数
         */
        private long uploadExpireSeconds = 600L;

        /**
         * 下载预签名默认过期秒数
         */
        private long downloadExpireSeconds = 1800L;

        /**
         * 已用 token 在 Redis 里保留的秒数
         */
        private long usedTokenTtlSeconds = 1800L;
    }


}
