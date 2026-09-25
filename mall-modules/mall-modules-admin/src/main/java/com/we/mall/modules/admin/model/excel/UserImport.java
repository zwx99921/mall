package com.we.mall.modules.admin.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户导入
 *
 * @author we
 * @date 2026-09-23
 * @description
 */
@Data
public class UserImport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

}
