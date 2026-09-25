package com.we.mall.common.session.util;

import java.security.SecureRandom;

/**
 * SessionId 生成器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class SessionIdGenerator {

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private SessionIdGenerator() {
    }

    /**
     * 生成指定长度的 sessionId
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

}
