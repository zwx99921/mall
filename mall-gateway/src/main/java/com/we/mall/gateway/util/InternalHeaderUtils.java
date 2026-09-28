package com.we.mall.gateway.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.context.SessionContext;
import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 内部 header 构造器
 * <p>
 * 把 {@link SessionInfo} 转成网关下发的 X-Internal-* header。
 *
 * @author we
 * @date 2026-09-22
 * @description
 */
public final class InternalHeaderUtils {

    private InternalHeaderUtils() {
    }

    public static ServerHttpRequest buildRequest(ServerHttpRequest request, SessionContext context) {

        SessionInfo sessionInfo = context.getSession();
        SessionUser sessionUser = context.getUser();

        return request.mutate()
                .header(HeaderConstants.HEADER_INTERNAL_SESSION_ID, StrUtil.emptyIfNull(sessionInfo.getSessionId()))
                .header(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE, StrUtil.emptyIfNull(sessionInfo.getClientType()))
                .header(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE, StrUtil.emptyIfNull(sessionInfo.getDeviceType()))

                .header(HeaderConstants.HEADER_INTERNAL_USER_ID, String.valueOf(sessionUser.getUserId()))
                .header(HeaderConstants.HEADER_INTERNAL_NICKNAME, StrUtil.emptyIfNull(sessionUser.getNickname()))
                .header(HeaderConstants.HEADER_INTERNAL_USERNAME, StrUtil.emptyIfNull(sessionUser.getUsername()))
                .header(HeaderConstants.HEADER_INTERNAL_ROLES, CollUtil.join(sessionUser.getRoles(), ","))
                .header(HeaderConstants.HEADER_INTERNAL_PERMS, CollUtil.join(sessionUser.getPerms(), ","))
                .build();
    }

}
