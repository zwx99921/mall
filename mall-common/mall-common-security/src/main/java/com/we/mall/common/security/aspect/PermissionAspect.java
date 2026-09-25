package com.we.mall.common.security.aspect;

import com.we.mall.common.core.exception.ForbiddenException;
import com.we.mall.common.security.annotation.RequiresLogin;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.common.security.annotation.RequiresRole;
import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.security.enums.Logical;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限校验切面
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Aspect
public class PermissionAspect {

    @Before("@annotation(requiresLogin) || @within(requiresLogin)")
    public void checkLogin(RequiresLogin requiresLogin) {
        SecurityContext.requireLogin();
    }

    @Before("@annotation(requiresRole)")
    public void checkRole(RequiresRole requiresRole) {
        Set<String> roles = SecurityContext.getRoles();
        if (notMatch(roles, requiresRole.value(), requiresRole.logical())) {
            throw ForbiddenException.of("角色不足");
        }
    }

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        Set<String> perms = SecurityContext.getPerms();
        if (notMatch(perms, requiresPermission.value(), requiresPermission.logical())) {
            throw ForbiddenException.of("权限不足");
        }
    }

    /**
     * 判断拥有的集合是否满足要求
     *
     * @return true 不满足（缺少角色/权限）
     */
    private boolean notMatch(Set<String> owned, String[] required, Logical logical) {
        if (CollectionUtils.isEmpty(owned)) {
            return true;
        }
        if (logical == Logical.OR) {
            return Arrays.stream(required).noneMatch(owned::contains);
        }
        return !Arrays.stream(required).allMatch(owned::contains);
    }

}
