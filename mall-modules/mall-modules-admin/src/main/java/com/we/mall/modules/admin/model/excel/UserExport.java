package com.we.mall.modules.admin.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户导出
 *
 * @author we
 * @date 2026-09-23
 * @description
 */

@Data
@ColumnWidth(20)
public class UserExport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("用户 ID")
    private Long userId;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("状态")
    private String statusDesc;

    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ExcelProperty("更新时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String updateTime;

}
