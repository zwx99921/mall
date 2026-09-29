package com.we.mall.common.jwt.service.impl;

import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.jwt.constant.JwtConstants;
import com.we.mall.common.jwt.enums.TokenType;
import com.we.mall.common.jwt.exception.TokenException;
import com.we.mall.common.jwt.properties.JwtProperties;
import com.we.mall.common.jwt.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Jwt 服务实现
 *
 * @author we
 * @date 2026-09-20
 * @description
 */

@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final ClientType clientType;
    private final SecretKey signingKey;

    public JwtServiceImpl(JwtProperties jwtProperties, ClientType clientType) {
        this.jwtProperties = Objects.requireNonNull(jwtProperties, "jwtProperties 不能为 null");
        this.clientType = Objects.requireNonNull(clientType, "clientType 不能为 null");
        this.signingKey = buildSigningKey();
    }

    // ==================== 生成 ====================

    @Override
    public String createAccessToken(Long userId, String username,
                                    Set<String> roles, Set<String> perms,
                                    String sessionId) {
        Claims claims = Jwts.claims();
        claims.put(JwtConstants.CLAIM_CLIENT_TYPE, clientType.getCode());
        claims.put(JwtConstants.CLAIM_TOKEN_TYPE, TokenType.ACCESS.name());
        claims.put(JwtConstants.CLAIM_USERNAME, username);
        if (!CollectionUtils.isEmpty(roles)) {
            claims.put(JwtConstants.CLAIM_ROLES, roles);
        }
        if (!CollectionUtils.isEmpty(perms)) {
            claims.put(JwtConstants.CLAIM_PERMS, perms);
        }
        if (StringUtils.hasText(sessionId)) {
            claims.put(JwtConstants.CLAIM_SESSION_ID, sessionId);
        }
        return createToken(userId, getConfig().getExpire(), claims);
    }

    @Override
    public String createRefreshToken(Long userId, String username, String sessionId) {
        Claims claims = Jwts.claims();
        claims.put(JwtConstants.CLAIM_CLIENT_TYPE, clientType.getCode());
        claims.put(JwtConstants.CLAIM_TOKEN_TYPE, TokenType.REFRESH.name());
        claims.put(JwtConstants.CLAIM_USERNAME, username);
        if (StringUtils.hasText(sessionId)) {
            claims.put(JwtConstants.CLAIM_SESSION_ID, sessionId);
        }
        return createToken(userId, getConfig().getRefreshExpire(), claims);
    }

    // ==================== 解析 ====================

    @Override
    public Claims parse(String token) {
        if (!StringUtils.hasText(token)) {
            throw TokenException.invalid("token 为空");
        }
        try {
            JwtParserBuilder builder = Jwts.parserBuilder().setSigningKey(signingKey);

            String issuer = getConfig().getIssuer();
            if (StringUtils.hasText(issuer)) {
                builder.requireIssuer(issuer);
            }

            return builder.build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.debug("JWT 已过期: {}", e.getMessage(), e);
            throw TokenException.expired();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT 解析失败: {}", e.getMessage(), e);
            throw TokenException.invalid();
        }
    }

    @Override
    public Claims parseQuietly(String token) {
        try {
            return parse(token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean validate(String token) {
        return parseQuietly(token) != null;
    }

    // ==================== Claim 读取 ====================

    @Override
    public Long getUserId(String token) {
        String subject = parse(token).getSubject();
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw TokenException.invalid("token subject 不是有效用户 ID");
        }
    }

    @Override
    public String getUsername(String token) {
        return parse(token).get(JwtConstants.CLAIM_USERNAME, String.class);
    }

    @Override
    public Set<String> getRoles(String token) {
        return toSet(parse(token).get(JwtConstants.CLAIM_ROLES));
    }

    @Override
    public Set<String> getPerms(String token) {
        return toSet(parse(token).get(JwtConstants.CLAIM_PERMS));
    }

    @Override
    public String getSessionId(String token) {
        return parse(token).get(JwtConstants.CLAIM_SESSION_ID, String.class);
    }

    @Override
    public String getTokenType(String token) {
        return parse(token).get(JwtConstants.CLAIM_TOKEN_TYPE, String.class);
    }

    @Override
    public ClientType getUserType() {
        return clientType;
    }

    // ==================== 配置读取 ====================

    @Override
    public Long getExpire() {
        return getConfig().getExpire();
    }

    @Override
    public Long getExpireSeconds() {
        Long expire = getExpire();
        return expire == null ? 0L : expire / 1000;
    }

    @Override
    public Long getRefreshExpire() {
        return getConfig().getRefreshExpire();
    }

    @Override
    public Long getRefreshExpireSeconds() {
        Long expire = getRefreshExpire();
        return expire == null ? 0L : expire / 1000;
    }

    // ==================== 私有方法 ====================

    private JwtProperties.Config getConfig() {
        return clientType == ClientType.ADMIN ? jwtProperties.getAdmin() : jwtProperties.getMember();
    }

    private SecretKey buildSigningKey() {
        byte[] keyBytes = getConfig().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(long userId, long expireMillis, Claims claims) {
        long now = System.currentTimeMillis();
        JwtProperties.Config config = getConfig();
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .setIssuer(config.getIssuer())
                .compact();
    }

    /**
     * 将 claim 值转成 Set<String>
     * <p>
     * 兼容 List / Set / 单值。
     * 使用 {@code Collection<?>} 通配符，不涉及泛型强转。
     */
    private Set<String> toSet(Object value) {
        if (value == null) {
            return Collections.emptySet();
        }
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Set<String> result = new HashSet<>(collection.size());
            for (Object o : collection) {
                if (o != null) {
                    result.add(String.valueOf(o));
                }
            }
            return result;
        }
        return Collections.singleton(String.valueOf(value));
    }

}
