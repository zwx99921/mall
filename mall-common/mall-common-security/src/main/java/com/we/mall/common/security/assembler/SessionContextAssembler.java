package com.we.mall.common.security.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * 上下文装配器
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
@Slf4j
public final class SessionContextAssembler {

    private SessionContextAssembler() {
    }

    /**
     * 从请求 header 还原安全上下文
     *
     * @param request 请求
     * @return SecurityContext；关键 header 缺失时返回 null
     */
    public static SessionContext fromHeaders(HttpServletRequest request) {
        String userId = request.getHeader(HeaderConstants.HEADER_INTERNAL_USER_ID);
        if (StrUtil.isBlank(userId)) {
            return null;
        }

        log.info("SessionInfoAssembler: userId={}, path={}, allHeaders={}",
                userId, request.getRequestURI(),
                Collections.list(request.getHeaderNames()));

        // 构造 SessionUser
        SessionUser user = SessionUser.builder()
                .userId(Convert.toLong(userId))
                .username(request.getHeader(HeaderConstants.HEADER_INTERNAL_USERNAME))
                .nickname(request.getHeader(HeaderConstants.HEADER_INTERNAL_NICKNAME))
//                .avatar(request.getHeader(HeaderConstants.HEADER_INTERNAL_AVATAR))
//                .clientType(request.getHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE))
                .roles(CollUtil.newHashSet(StrUtil.splitTrim(request.getHeader(HeaderConstants.HEADER_INTERNAL_ROLES), ',')))
                .perms(CollUtil.newHashSet(StrUtil.splitTrim(request.getHeader(HeaderConstants.HEADER_INTERNAL_PERMS), ',')))
                .build();

        // 构造 SessionInfo
        SessionInfo session = SessionInfo.builder()
                .sessionId(request.getHeader(HeaderConstants.HEADER_INTERNAL_SESSION_ID))
                .clientType(request.getHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE))
                .deviceType(request.getHeader(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE))
                .deviceName(request.getHeader(HeaderConstants.HEADER_DEVICE_NAME))
                .userId(Convert.toLong(userId))
                .build();

        // 构造 SecurityContext
        return SessionContext.builder()
                .session(session)
                .user(user)
                .build();
    }

}
