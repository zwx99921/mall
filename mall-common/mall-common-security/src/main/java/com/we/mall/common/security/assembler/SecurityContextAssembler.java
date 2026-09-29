package com.we.mall.common.security.assembler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.core.enums.ClientType;
import com.we.mall.common.security.context.SecurityContext;
import com.we.mall.common.session.model.SessionUser;
import com.we.mall.common.session.service.SessionService;
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
public class SecurityContextAssembler {

    private final SessionService sessionService;

    public SecurityContextAssembler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * 从请求 header 还原安全上下文
     *
     * @param request 请求
     * @return SecurityContext；关键 header 缺失时返回 null
     */
    public SecurityContext loadFromGateway(HttpServletRequest request) {
        if (sessionService == null) {
            log.warn("SessionService 不可用，网关模式无法还原上下文");
            return null;
        }

        String sessionId = request.getHeader(HeaderConstants.HEADER_INTERNAL_SESSION_ID);
        String userIdStr = request.getHeader(HeaderConstants.HEADER_INTERNAL_USER_ID);
        String clientTypeCode = request.getHeader(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE);
        String deviceType = request.getHeader(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE);

        if (StrUtil.isBlank(sessionId)
                || StrUtil.isBlank(userIdStr)
                || StrUtil.isBlank(clientTypeCode)) {
            return null;
        }

        Long userId = Convert.toLong(userIdStr);
        ClientType clientType = ClientType.of(clientTypeCode);
        if (clientType == null) {
            return null;
        }

        // 查 SessionUser（拿 nickname / avatar / roles / perms）
        SessionUser user = sessionService.getSessionUser(clientType, userId);
        if (user == null) {
            return null;
        }

        log.info("SessionInfoAssembler: userId={}, path={}, allHeaders={}",
                userId, request.getRequestURI(),
                Collections.list(request.getHeaderNames()));

        return SecurityContext.builder()
                .sessionId(sessionId)
                .clientType(clientTypeCode)
                .deviceType(deviceType)
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(user.getRoles())
                .perms(user.getPerms())
                .build();
    }

}
