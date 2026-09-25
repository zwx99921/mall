package com.we.mall.modules.admin.controller.admin;

import com.we.mall.common.core.result.R;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.modules.admin.model.request.RoleCreateRequest;
import com.we.mall.modules.admin.model.request.RoleMenuRequest;
import com.we.mall.modules.admin.model.request.RolePageRequest;
import com.we.mall.modules.admin.model.request.RoleUpdateRequest;
import com.we.mall.modules.admin.model.response.RoleResponse;
import com.we.mall.modules.admin.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 角色管理 接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Tag(name = "角色管理", description = "角色 CURD")
@Slf4j
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Validated
public class AdminRoleController {

    private final RoleService roleService;

    @Operation(summary = "角色分页")
    @GetMapping("/page")
    @RequiresPermission("admin:role:list")
    public R<PageResult<RoleResponse>> page(@Valid RolePageRequest request) {
        return R.ok(roleService.page(request));
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    @RequiresPermission("admin:role:detail")
    public R<RoleResponse> detail(@PathVariable Long id) {
        return R.ok(roleService.detail(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @RequiresPermission("admin:role:create")
    public R<Long> create(@RequestBody @Valid RoleCreateRequest request) {
        return R.ok(roleService.create(request));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    @RequiresPermission("admin:role:update")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody @Valid RoleUpdateRequest request) {
        roleService.update(id, request);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @RequiresPermission("admin:role:delete")
    public R<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return R.ok();
    }

    @Operation(summary = "全部角色")
    @GetMapping("/list")
    @RequiresPermission("admin:role:list")
    public R<List<RoleResponse>> list() {
        return R.ok(roleService.listAll());
    }

    @Operation(summary = "角色菜单")
    @GetMapping("/{id}/menus")
    @RequiresPermission("admin:role:detail")
    public R<Set<Long>> getMenuIds(@PathVariable Long id) {
        return R.ok(roleService.getMenuIds(id));
    }

    @Operation(summary = "分配菜单")
    @PutMapping("/{id}/menus")
    @RequiresPermission("admin:role:assign")
    public R<Void> assignMenus(@PathVariable Long id, @RequestBody @Valid RoleMenuRequest request) {
        roleService.assignMenus(id, request.getMenuIds());
        return R.ok();
    }

}
