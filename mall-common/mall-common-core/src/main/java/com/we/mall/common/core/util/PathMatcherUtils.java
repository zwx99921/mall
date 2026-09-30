package com.we.mall.common.core.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 路径匹配工具
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public final class PathMatcherUtils {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private PathMatcherUtils() {
    }

    public static boolean match(String pattern, String path) {
        return MATCHER.match(pattern, path);
    }

    public static boolean matchesAny(Collection<String> patterns, String path) {
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesAll(Collection<String> patterns, String path) {
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (!MATCHER.match(pattern, path)) {
                return false;
            }
        }
        return true;
    }

}
