package com.we.mall.modules.admin.controller.admin;

import com.we.mall.common.core.result.R;
import com.we.mall.common.mybatis.result.PageResult;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.modules.admin.model.request.OnlinePageRequest;
import com.we.mall.modules.admin.model.response.OnlineSessionResponse;
import com.we.mall.modules.admin.model.response.OnlineUserResponse;
import com.we.mall.modules.admin.service.OnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 在线用户 接口
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Tag(name = "在线用户", description = "在线用户管理")
@Slf4j
@RestController
@RequestMapping("/admin/online")
@RequiredArgsConstructor
@Validated
public class AdminOnlineController {

    private final OnlineService onlineService;

    @Operation(summary = "在线用户分页")
    @GetMapping("/page")
    @RequiresPermission("admin:online:list")
    public R<PageResult<OnlineUserResponse>> page(@Valid OnlinePageRequest request) {
        return R.ok(onlineService.page(request));
    }

    @Operation(summary = "在线设备列表")
    @GetMapping("/{userId}/sessions")
    @RequiresPermission("admin:online:list")
    public R<List<OnlineSessionResponse>> sessions(@PathVariable Long userId) {
        return R.ok(onlineService.sessions(userId));
    }

    @Operation(summary = "用户下线")
    @DeleteMapping("/{userId}/kick")
    @RequiresPermission("admin:online:kick")
    public R<Void> kick(@PathVariable Long userId) {
        onlineService.kick(userId);
        return R.ok();
    }

    @Operation(summary = "会话下线")
    @DeleteMapping("/session/{sessionId}/kick")
    @RequiresPermission("admin:online:kick")
    public R<Void> kickSession(@PathVariable String sessionId) {
        onlineService.kickSession(sessionId);
        return R.ok();
    }

}
