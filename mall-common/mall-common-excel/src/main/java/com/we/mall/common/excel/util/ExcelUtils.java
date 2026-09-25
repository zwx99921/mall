package com.we.mall.common.excel.util;

import com.alibaba.excel.EasyExcel;
import com.we.mall.common.core.enums.ResultCode;
import com.we.mall.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 工具类
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Slf4j
public final class ExcelUtils {

    private ExcelUtils() {
    }

    // ==================== 导出 ====================

    /**
     * 导出 Excel
     *
     * @param response  响应
     * @param fileName  文件名（不含后缀）
     * @param sheetName sheet 名
     * @param clazz     行类型
     * @param data      数据
     */
    public static <T> void export(HttpServletResponse response,
                                  String fileName,
                                  String sheetName,
                                  Class<T> clazz,
                                  List<T> data) {
        try {
            // 1. 响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encodedFileName + ".xlsx");

            // 2. 写
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    .doWrite(data);

        } catch (IOException e) {
            log.error("导出 Excel 失败: fileName={}", fileName, e);
            throw BusinessException.of(ResultCode.EXPORT_ERROR);
        }
    }

    // ==================== 导入 ====================

    /**
     * 读 Excel（MultipartFile）
     */
    public static <T> List<T> read(MultipartFile file, Class<T> clazz) {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw BusinessException.of(ResultCode.IMPORT_FILE_EMPTY);
        }

        // 2. 校验后缀
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw BusinessException.of(ResultCode.IMPORT_FILE_TYPE_ERROR);
        }

        // 3. 读
        try (InputStream is = file.getInputStream()) {
            return EasyExcel.read(is)
                    .head(clazz)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            log.error("读取 Excel 失败: filename={}", filename, e);
            throw BusinessException.of(ResultCode.IMPORT_ERROR, "读取文件失败");
        }
    }


}
