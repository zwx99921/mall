package com.we.mall.common.jwt.service;

import com.we.mall.common.core.enums.UserType;
import com.we.mall.common.jwt.TestApplication;
import com.we.mall.common.core.constant.JwtConstants;
import com.we.mall.common.jwt.enums.TokenType;
import com.we.mall.common.jwt.exception.TokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtService 单元测试
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Jwt 服务测试")
public class JwtServiceTest {

    @Resource(name = "adminJwtService")
    private JwtService adminJwtService;

    @Resource(name = "memberJwtService")
    private JwtService memberJwtService;

    // ==================== Bean 加载 ====================

    @Test
    @DisplayName("双端 Bean 加载成功")
    void testBeansLoaded() {
        assertNotNull(adminJwtService);
        assertNotNull(memberJwtService);
        assertEquals(UserType.ADMIN, adminJwtService.getUserType());
        assertEquals(UserType.MEMBER, memberJwtService.getUserType());
    }

    // ==================== 生成 / 解析 access token ====================

    @Test
    @DisplayName("admin：生成 + 解析 access token")
    void testAdminAccessToken() {
        Set<String> roles = new HashSet<>(Arrays.asList("admin", "ops"));
        Set<String> perms = new HashSet<>(Arrays.asList("user:list", "user:delete"));

        String token = adminJwtService.createAccessToken(
                1L, "admin1", roles, perms, "sid-admin-1");
        assertNotNull(token);

        // 用户类型
        Claims claims = adminJwtService.parse(token);
        assertEquals(UserType.ADMIN.name(), claims.get(JwtConstants.CLAIM_USER_TYPE, String.class));
        assertEquals(TokenType.ACCESS.name(), claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class));

        // 字段读取
        assertEquals(1L, adminJwtService.getUserId(token));
        assertEquals("admin1", adminJwtService.getUsername(token));
        assertEquals(roles, adminJwtService.getRoles(token));
        assertEquals(perms, adminJwtService.getPerms(token));
        assertEquals("sid-admin-1", adminJwtService.getSessionId(token));
        assertEquals(TokenType.ACCESS.name(), adminJwtService.getTokenType(token));
    }

    @Test
    @DisplayName("member：生成 + 解析 access token")
    void testMemberAccessToken() {
        String token = memberJwtService.createAccessToken(
                1001L, "user1", null, null, "sid-member-1");
        assertNotNull(token);

        Claims claims = memberJwtService.parse(token);
        assertEquals(UserType.MEMBER.name(), claims.get(JwtConstants.CLAIM_USER_TYPE, String.class));

        assertEquals(1001L, memberJwtService.getUserId(token));
        assertEquals("user1", memberJwtService.getUsername(token));
        assertTrue(memberJwtService.getRoles(token).isEmpty());
        assertTrue(memberJwtService.getPerms(token).isEmpty());
        assertEquals("sid-member-1", memberJwtService.getSessionId(token));
    }

    @Test
    @DisplayName("access token 无 roles / perms / sessionId 时不写入 claim")
    void testAccessTokenWithoutOptionalClaims() {
        String token = memberJwtService.createAccessToken(1L, "u", null, null, null);
        Claims claims = memberJwtService.parse(token);

        assertNull(claims.get(JwtConstants.CLAIM_ROLES));
        assertNull(claims.get(JwtConstants.CLAIM_PERMS));
        assertNull(claims.get(JwtConstants.CLAIM_SESSION_ID));
    }

    // ==================== 生成 / 解析 refresh token ====================

    @Test
    @DisplayName("生成 + 解析 refresh token")
    void testRefreshToken() {
        String token = memberJwtService.createRefreshToken(1001L, "user1", "sid-r-1");
        assertNotNull(token);

        Claims claims = memberJwtService.parse(token);
        assertEquals(TokenType.REFRESH.name(), claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class));
        assertEquals("sid-r-1", memberJwtService.getSessionId(token));
        assertEquals(1001L, memberJwtService.getUserId(token));
    }

    @Test
    @DisplayName("refresh token 无 sessionId 时不写入")
    void testRefreshTokenWithoutSessionId() {
        String token = memberJwtService.createRefreshToken(1L, "u", null);
        assertNull(memberJwtService.parse(token).get(JwtConstants.CLAIM_SESSION_ID));
    }

    // ==================== 双端隔离 ====================

    @Test
    @DisplayName("admin token 无法被 member 解析")
    void testCrossClientIsolation() {
        String adminToken = adminJwtService.createAccessToken(1L, "admin", null, null, null);

        // admin 能解析
        assertTrue(adminJwtService.validate(adminToken));

        // member 不能解析（密钥不同）
        assertFalse(memberJwtService.validate(adminToken));
    }

    @Test
    @DisplayName("member token 无法被 admin 解析")
    void testCrossClientIsolationReverse() {
        String memberToken = memberJwtService.createAccessToken(1L, "user", null, null, null);

        assertTrue(memberJwtService.validate(memberToken));
        assertFalse(adminJwtService.validate(memberToken));
    }

    // ==================== validate / parseQuietly ====================

    @Test
    @DisplayName("validate 合法 token 返回 true")
    void testValidateValid() {
        String token = memberJwtService.createAccessToken(1L, "u", null, null, null);
        assertTrue(memberJwtService.validate(token));
    }

    @Test
    @DisplayName("validate 非法 token 返回 false")
    void testValidateInvalid() {
        assertFalse(memberJwtService.validate("not-a-token"));
        assertFalse(memberJwtService.validate(""));
        assertFalse(memberJwtService.validate(null));
    }

    @Test
    @DisplayName("parseQuietly 非法 token 返回 null")
    void testParseQuietlyInvalid() {
        assertNull(memberJwtService.parseQuietly("not-a-token"));
        assertNull(memberJwtService.parseQuietly(null));
    }

    @Test
    @DisplayName("parse 非法 token 抛 TokenException")
    void testParseInvalid() {
        assertThrows(TokenException.class, () -> memberJwtService.parse("not-a-token"));
    }

    @Test
    @DisplayName("parse 空 token 抛 TokenException")
    void testParseEmpty() {
        assertThrows(TokenException.class, () -> memberJwtService.parse(""));
        assertThrows(TokenException.class, () -> memberJwtService.parse(null));
    }

    // ==================== 篡改检测 ====================

    @Test
    @DisplayName("篡改 token 内容后解析失败")
    void testTamperedToken() {
        String token = memberJwtService.createAccessToken(1L, "u", null, null, null);

        // 改最后一位
        String tampered = token.substring(0, token.length() - 1)
                + (token.endsWith("A") ? "B" : "A");

        assertFalse(memberJwtService.validate(tampered));
    }

    @Test
    @DisplayName("伪造签名解析失败")
    void testForgedSignature() {
        // 用 admin 密钥生成的 token，member 无法验证
        String adminToken = adminJwtService.createAccessToken(1L, "u", null, null, null);
        assertFalse(memberJwtService.validate(adminToken));
    }

    // ==================== getUserId 非法 subject ====================

    // ==================== 配置读取 ====================

    @Test
    @DisplayName("getExpire / getRefreshExpire")
    void testGetExpire() {
        assertEquals(7200000L, adminJwtService.getExpire());
        assertEquals(604800000L, adminJwtService.getRefreshExpire());

        assertEquals(604800000L, memberJwtService.getExpire());
        assertEquals(2592000000L, memberJwtService.getRefreshExpire());
    }

}
