package com.we.mall.common.security.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;

/**
 * 会话装配器
 * <p>
 * 从网关下发的内部 header 还原 {@link SessionInfo}。
 * 网关已完成认证与 session 查询，这里只做「格式还原」。
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
public final class SessionInfoAssembler {

    private SessionInfoAssembler() {
    }

    /**
     * 从请求 header 还原会话
     *
     * @param request 请求
     * @return SessionInfo；关键 header 缺失时返回 null
     */
    public static SessionInfo fromHeaders(HttpServletRequest request) {
        String userId = request.getHeader(HeaderConstants.HEADER_INTERNAL_USER_ID);
        if (StrUtil.isBlank(userId)) {
            return null;
        }

        log.info("SessionInfoAssembler: userId={}, path={}, allHeaders={}", userId, request.getRequestURI(), Collections.list(request.getHeaderNames()));

        // 构造用户
        SessionUser user = SessionUser.builder()
                .userId(Convert.toLong(userId))
                .username(request.getHeader(HeaderConstants.HEADER_INTERNAL_USERNAME))
                .nickname(request.getHeader(HeaderConstants.HEADER_INTERNAL_NICKNAME))
                .tenantId(Convert.toLong(request.getHeader(HeaderConstants.HEADER_INTERNAL_TENANT_ID)))
                .build();

        // 构造会话
        return SessionInfo.builder()
                .user(user)
                .sessionId(request.getHeader(HeaderConstants.HEADER_INTERNAL_SESSION_ID))
                .clientType(request.getHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE))
                .deviceType(request.getHeader(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE))
                .deviceName(request.getHeader(HeaderConstants.HEADER_DEVICE_NAME))
                .roles(parseSet(request.getHeader(HeaderConstants.HEADER_INTERNAL_ROLES)))
                .perms(parseSet(request.getHeader(HeaderConstants.HEADER_INTERNAL_PERMS)))
                .build();
    }

    /**
     * 解析逗号分隔的集合
     */
    private static Set<String> parseSet(String value) {
        return StrUtil.isBlank(value) ? Collections.emptySet() : CollUtil.newHashSet(StrUtil.splitTrim(value, ','));
    }

}
