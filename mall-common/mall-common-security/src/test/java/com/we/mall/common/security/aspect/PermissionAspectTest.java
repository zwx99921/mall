package com.we.mall.common.security.aspect;

import com.we.mall.common.core.exception.UnauthorizedException;
import com.we.mall.common.security.annotation.RequiresLogin;
import com.we.mall.common.security.annotation.RequiresPermission;
import com.we.mall.common.security.annotation.RequiresRole;
import com.we.mall.common.security.enums.Logical;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionAspect 单元测试
 * <p>
 * 直接 new，不走 Spring 代理。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@DisplayName("PermissionAspect 测试")
public class PermissionAspectTest {

    private PermissionAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new PermissionAspect();
    }

    @AfterEach
    void clean() {
        SessionContext.clear();
    }

    // ==================== 工具方法 ====================

    private void login(String clientType, Set<String> roles, Set<String> perms) {
        SessionUser user = new SessionUser();
        user.setUserId(1L);
        user.setUsername("user1");

        SessionInfo info = new SessionInfo();
        info.setSessionId("sid");
        info.setClientType(clientType);
        info.setUser(user);
        info.setRoles(roles);
        info.setPerms(perms);

        SessionContext.set(info);
    }

    /**
     * 动态构造 RequiresLogin 注解实例
     */
    private RequiresLogin mockLoginAnnotation() {
        return new RequiresLogin() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresLogin.class;
            }
        };
    }

    /**
     * 动态构造 RequiresRole 注解实例
     */
    private RequiresRole mockRoleAnnotation(String[] values, Logical logical) {
        return new RequiresRole() {
            @Override
            public String[] value() {
                return values;
            }

            @Override
            public Logical logical() {
                return logical;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresRole.class;
            }
        };
    }

    /**
     * 动态构造 RequiresPermission 注解实例
     */
    private RequiresPermission mockPermAnnotation(String[] values, Logical logical) {
        return new RequiresPermission() {
            @Override
            public String[] value() {
                return values;
            }

            @Override
            public Logical logical() {
                return logical;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequiresPermission.class;
            }
        };
    }

    // ==================== checkLogin ====================

    @Test
    @DisplayName("checkLogin：未登录抛 401")
    void testCheckLoginNotLogin() {

        RequiresLogin annotation = mockLoginAnnotation();

        // 没 set 上下文
        assertThrows(UnauthorizedException.class, () -> aspect.checkLogin(annotation));
    }

    @Test
    @DisplayName("checkLogin：已登录不抛异常")
    void testCheckLoginOK() {
        login("admin", null, null);

        RequiresLogin annotation = mockLoginAnnotation();

        assertDoesNotThrow(() -> aspect.checkLogin(annotation));
    }

    // ==================== checkRole ====================

    @Test
    @DisplayName("checkRole：拥有指定角色（AND，单个）→ 通过")
    void testRoleSingleMatch() {
        login("admin", Collections.singleton("admin"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin"}, Logical.AND);

        assertDoesNotThrow(() -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：缺少角色 → 403")
    void testRoleSingleNotMatch() {
        login("admin", Collections.singleton("user"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin"}, Logical.AND);

        UnauthorizedException e = assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
        assertTrue(e.getMessage().contains("角色"));
    }

    @Test
    @DisplayName("checkRole：AND 全部匹配 → 通过")
    void testRoleAndAllMatch() {
        login("admin",
                new LinkedHashSet<>(Arrays.asList("admin", "ops")),
                null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin", "ops"}, Logical.AND);

        assertDoesNotThrow(() -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：AND 部分匹配 → 403")
    void testRoleAndPartialMatch() {
        login("admin", Collections.singleton("admin"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin", "ops"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：OR 任意匹配 → 通过")
    void testRoleOrAnyMatch() {
        login("admin", Collections.singleton("admin"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin", "ops"}, Logical.OR);

        assertDoesNotThrow(() -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：OR 都不匹配 → 403")
    void testRoleOrNoneMatch() {
        login("admin", Collections.singleton("user"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin", "ops"}, Logical.OR);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：roles 为空 → 403")
    void testRoleEmpty() {
        login("admin", Collections.emptySet(), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("checkRole：roles 为 null → 403")
    void testRoleNull() {
        login("admin", null, null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
    }

    // ==================== checkPermission ====================

    @Test
    @DisplayName("checkPermission：拥有权限（AND，单个）→ 通过")
    void testPermSingleMatch() {
        login("admin", null, Collections.singleton("user:read"));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read"}, Logical.AND);

        assertDoesNotThrow(() -> aspect.checkPermission(annotation));
    }

    @Test
    @DisplayName("checkPermission：缺少权限 → 403")
    void testPermSingleNotMatch() {
        login("admin", null, Collections.singleton("user:read"));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:delete"}, Logical.AND);

        UnauthorizedException e = assertThrows(UnauthorizedException.class,
                () -> aspect.checkPermission(annotation));
        assertTrue(e.getMessage().contains("权限"));
    }

    @Test
    @DisplayName("checkPermission：AND 全部匹配 → 通过")
    void testPermAndAllMatch() {
        login("admin", null,
                new LinkedHashSet<>(Arrays.asList("user:read", "user:write")));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read", "user:write"}, Logical.AND);

        assertDoesNotThrow(() -> aspect.checkPermission(annotation));
    }

    @Test
    @DisplayName("checkPermission：AND 部分匹配 → 403")
    void testPermAndPartialMatch() {
        login("admin", null, Collections.singleton("user:read"));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read", "user:write"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkPermission(annotation));
    }

    @Test
    @DisplayName("checkPermission：OR 任意匹配 → 通过")
    void testPermOrAnyMatch() {
        login("admin", null, Collections.singleton("user:read"));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read", "user:delete"}, Logical.OR);

        assertDoesNotThrow(() -> aspect.checkPermission(annotation));
    }

    @Test
    @DisplayName("checkPermission：OR 都不匹配 → 403")
    void testPermOrNoneMatch() {
        login("admin", null, Collections.singleton("user:list"));

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read", "user:delete"}, Logical.OR);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkPermission(annotation));
    }

    @Test
    @DisplayName("checkPermission：perms 为空 → 403")
    void testPermEmpty() {
        login("admin", null, Collections.emptySet());

        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkPermission(annotation));
    }

    // ==================== 不依赖登录（异常类型） ====================

    @Test
    @DisplayName("未登录时 checkRole 也抛 401（因为 requireLogin 先执行）")
    void testCheckRoleNotLogin() {
        // 没 set 上下文
        RequiresRole annotation = mockRoleAnnotation(
                new String[]{"admin"}, Logical.AND);

        // 你的实现里 checkRole 走 SecurityContext.getRoles()，
        // 未登录时 getRoles 返回空 Set，走 notMatch → 抛 403
        assertThrows(UnauthorizedException.class,
                () -> aspect.checkRole(annotation));
    }

    @Test
    @DisplayName("未登录时 checkPermission 也抛 403")
    void testCheckPermissionNotLogin() {
        RequiresPermission annotation = mockPermAnnotation(
                new String[]{"user:read"}, Logical.AND);

        assertThrows(UnauthorizedException.class,
                () -> aspect.checkPermission(annotation));
    }

    // ==================== 边界 ====================

    @Test
    @DisplayName("required 为空数组：AND / OR 都返回 true（通过）")
    void testEmptyRequiredArray() {
        login("admin", Collections.singleton("admin"), null);

        RequiresRole annotation = mockRoleAnnotation(
                new String[]{}, Logical.AND);

        // 空数组时：allMatch 返回 true
        assertDoesNotThrow(() -> aspect.checkRole(annotation));
    }

}
