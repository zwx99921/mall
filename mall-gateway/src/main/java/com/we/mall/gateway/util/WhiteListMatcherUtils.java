package com.we.mall.gateway.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 白名单匹配器
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
public final class WhiteListMatcherUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private WhiteListMatcherUtils() {
    }

    /**
     * 路径是否命中白名单
     */
    public static boolean match(List<String> whiteList, String path) {
        if (CollectionUtils.isEmpty(whiteList)) {
            return false;
        }
        return whiteList.stream().anyMatch(p -> PATH_MATCHER.match(p, path));
    }

}
