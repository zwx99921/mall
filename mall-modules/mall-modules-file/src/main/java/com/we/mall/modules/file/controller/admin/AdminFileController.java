package com.we.mall.modules.file.controller.admin;

import cn.hutool.core.io.resource.InputStreamResource;
import com.we.mall.common.core.result.R;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.modules.file.model.request.FilePageRequest;
import com.we.mall.modules.file.model.request.FilePresignUploadRequest;
import com.we.mall.modules.file.model.response.FilePresignResponse;
import com.we.mall.modules.file.model.response.FileResponse;
import com.we.mall.modules.file.model.response.FileUploadResponse;
import com.we.mall.modules.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理 接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Tag(name = "文件管理", description = "文件上传/下载/删除")
@Slf4j
@RestController
@RequestMapping("/admin/file")
@RequiredArgsConstructor
@Validated
public class AdminFileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @RequiresPermission("admin:file:upload")
    public R<FileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "bizType", required = false) String bizType) {
        return R.ok(fileService.upload(file, bizType));
    }

    @Operation(summary = "获取上传预签名")
    @PostMapping("/presign-upload")
    @RequiresPermission("admin:file:upload")
    public R<FilePresignResponse> presignUpload(@RequestBody @Valid FilePresignUploadRequest request) {
        return R.ok(fileService.presignUpload(request));
    }

    @Operation(summary = "获取下载预签名")
    @GetMapping("/{id}/presign-download")
    @RequiresPermission("admin:file:download")
    public R<FilePresignResponse> presignDownload(@PathVariable Long id) {
        return R.ok(fileService.presignDownload(id));
    }

    @Operation(summary = "下载文件")
    @GetMapping("/{id}")
    @RequiresPermission("admin:file:download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) {
        FileService.FileDownload download = fileService.download(id);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(download.getFileName(), StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentType(MediaType.parseMediaType(
                download.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : download.getContentType()));
        headers.setContentLength(download.getSize());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(download.getInputStream()));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    @RequiresPermission("admin:file:delete")
    public R<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return R.ok();
    }

    @Operation(summary = "文件分页")
    @GetMapping("/page")
    @RequiresPermission("admin:file:list")
    public R<PageResult<FileResponse>> page(@Valid FilePageRequest request) {
        return R.ok(fileService.page(request));
    }

}
