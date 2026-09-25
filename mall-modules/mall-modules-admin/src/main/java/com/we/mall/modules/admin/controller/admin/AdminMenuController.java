package com.we.mall.modules.admin.controller.admin;

import com.we.mall.common.core.result.R;
import com.we.mall.common.security.annotation.RequiresLogin;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.modules.admin.model.request.MenuCreateRequest;
import com.we.mall.modules.admin.model.request.MenuUpdateRequest;
import com.we.mall.modules.admin.model.response.MenuResponse;
import com.we.mall.modules.admin.model.response.MenuTreeResponse;
import com.we.mall.modules.admin.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜单管理 接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Tag(name = "菜单管理", description = "菜单 CURD")
@Slf4j
@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
@Validated
public class AdminMenuController {

    private final MenuService menuService;

    @Operation(summary = "用户路由")
    @GetMapping("/routes")
    @RequiresLogin
    public R<List<MenuTreeResponse>> routes() {
        return R.ok(menuService.routes());
    }

    @Operation(summary = "菜单树")
    @GetMapping("/tree")
    @RequiresPermission("admin:menu:list")
    public R<List<MenuTreeResponse>> tree() {
        return R.ok(menuService.tree());
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    @RequiresPermission("admin:menu:detail")
    public R<MenuResponse> detail(@PathVariable Long id) {
        return R.ok(menuService.detail(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @RequiresPermission("admin:menu:create")
    public R<Long> create(@RequestBody @Valid MenuCreateRequest request) {
        return R.ok(menuService.create(request));
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/{id}")
    @RequiresPermission("admin:menu:update")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody @Valid MenuUpdateRequest request) {
        menuService.update(id, request);
        return R.ok();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @RequiresPermission("admin:menu:delete")
    public R<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return R.ok();
    }

}
