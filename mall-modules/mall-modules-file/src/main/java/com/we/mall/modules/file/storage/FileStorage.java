package com.we.mall.modules.file.storage;

import java.io.InputStream;

/**
 * 文件存储抽象
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public interface FileStorage {

    /**
     * 服务端中转上传
     *
     * @param in           输入流
     * @param originalName 原始文件名
     * @param size         文件大小
     * @param contentType  MIME 类型
     * @return 存储 key
     */
    String upload(InputStream in, String originalName, long size, String contentType);

    /**
     * 服务端中转下载
     *
     * @param key 存储 key
     * @return 输入流
     */
    InputStream download(String key);

    /**
     * 删除
     *
     * @param key 存储 key
     */
    void delete(String key);

    /**
     * 生成上传预签名 URL
     * <p>
     * 本地实现：暂不支持，抛 UnsupportedOperationException。
     * OSS/MinIO：返回真正的预签名 URL。
     *
     * @param key       存储 key
     * @param expireSec 过期秒数
     * @return 预签名 URL
     */
    String presignUpload(String key, long expireSec);

    /**
     * 生成下载预签名 URL
     *
     * @param key       存储 key
     * @param expireSec 过期秒数
     * @return 预签名 URL
     */
    String presignDownload(String key, long expireSec);

    /**
     * 存储类型标识
     */
    String type();

}
