package com.we.mall.common.jwt.util;

import cn.hutool.json.JSONUtil;
import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.jwt.constant.JwtConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Jwt 工具类
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@Slf4j
public final class JwtUtils {

    private JwtUtils() {
    }

    /**
     * 从 Authorization 头解析 Bearer token
     *
     * @param authorization Authorization 头
     * @return token，无则返回 null
     */
    public static String parseBearer(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        if (!authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return null;
        }
        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    /**
     * 从 token payload 提取 userType（不校验签名）
     */
    public static ClientType extractClientType(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String clientType = JSONUtil.parseObj(payload).getStr(JwtConstants.CLAIM_CLIENT_TYPE);
            return ClientType.of(clientType);
        } catch (Exception e) {
            log.debug("extract userType fail: {}", e.getMessage());
            return null;
        }
    }

}
