package com.we.mall.modules.admin.controller.admin;

import com.we.mall.common.core.result.R;
import com.we.mall.common.excel.result.ImportResult;
import com.we.mall.common.excel.util.ExcelUtils;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.security.annotation.RequiresLogin;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.common.sensitive.annotation.SensitiveScene;
import com.we.mall.common.sensitive.enums.SensitiveSceneType;
import com.we.mall.modules.admin.model.excel.UserExport;
import com.we.mall.modules.admin.model.request.*;
import com.we.mall.modules.admin.model.response.UserInfoResponse;
import com.we.mall.modules.admin.model.response.UserResponse;
import com.we.mall.modules.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 用户管理 接口
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
@Tag(name = "用户管理", description = "用户 CURD")
@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "用户信息")
    @GetMapping("/info")
    @RequiresLogin
    public R<UserInfoResponse> info() {
        return R.ok(userService.info());
    }

    @Operation(summary = "用户分页")
    @GetMapping("/page")
    @RequiresPermission("admin:user:list")
    @SensitiveScene(SensitiveSceneType.PAGE)
    public R<PageResult<UserResponse>> page(@Valid UserPageRequest request) {
        return R.ok(userService.page(request));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @RequiresPermission("admin:user:detail")
    @SensitiveScene(SensitiveSceneType.DETAIL)
    public R<UserResponse> detail(@PathVariable Long id) {
        return R.ok(userService.detail(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @RequiresPermission("admin:user:create")
    public R<Long> create(@RequestBody @Valid UserCreateRequest request) {
        return R.ok(userService.create(request));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    @RequiresPermission("admin:user:update")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody @Valid UserUpdateRequest request) {
        userService.update(id, request);
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @RequiresPermission("admin:user:delete")
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }

    @Operation(summary = "修改状态")
    @PatchMapping("/{id}/status")
    @RequiresPermission("admin:user:status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody @Valid UserStatusRequest request) {
        userService.updateStatus(id, request.getStatus());
        return R.ok();
    }

    @Operation(summary = "修改密码")
    @PatchMapping("/{id}/password")
    @RequiresPermission("admin:user:password")
    public R<Void> updatePassword(@PathVariable Long id,
                                  @RequestBody @Valid UserPasswordRequest request) {
        userService.updatePassword(id, request.getPassword());
        return R.ok();
    }

    @Operation(summary = "用户角色")
    @GetMapping("/{id}/roles")
    @RequiresPermission("admin:user:detail")
    public R<Set<Long>> getRoleIds(@PathVariable Long id) {
        return R.ok(userService.getRoleIds(id));
    }

    @Operation(summary = "分配角色")
    @PutMapping("/{id}/roles")
    @RequiresPermission("admin:user:assign")
    public R<Void> assignRoles(@PathVariable Long id,
                               @RequestBody @Valid UserRoleRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return R.ok();
    }

    @Operation(summary = "用户导出")
    @GetMapping("/export")
    @RequiresPermission("admin:user:export")
    public void exports(HttpServletResponse response, UserPageRequest request) {
        List<UserExport> exports = userService.exports(request);
        ExcelUtils.export(response, "用户列表", "用户", UserExport.class, exports);
    }

    @Operation(summary = "用户导入")
    @PostMapping("/import")
    @RequiresPermission("admin:user:import")
    public R<ImportResult> imports(@RequestParam("file") MultipartFile file) throws IOException {
        return R.ok(userService.imports(file));
    }

}