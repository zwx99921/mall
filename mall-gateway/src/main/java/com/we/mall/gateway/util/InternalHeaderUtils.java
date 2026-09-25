package com.we.mall.gateway.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
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

    public static ServerHttpRequest buildRequest(ServerHttpRequest request, SessionInfo info) {
        SessionUser user = info.getUser();

        return request.mutate()
                .header(HeaderConstants.HEADER_INTERNAL_USER_ID, String.valueOf(user.getUserId()))
                .header(HeaderConstants.HEADER_INTERNAL_USERNAME, StrUtil.emptyIfNull(user.getUsername()))
                .header(HeaderConstants.HEADER_INTERNAL_NICKNAME, StrUtil.emptyIfNull(user.getNickname()))
                .header(HeaderConstants.HEADER_INTERNAL_TENANT_ID, user.getTenantId() == null ? "" : String.valueOf(user.getTenantId()))
                .header(HeaderConstants.HEADER_INTERNAL_SESSION_ID, StrUtil.emptyIfNull(info.getSessionId()))
                .header(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE, StrUtil.emptyIfNull(info.getClientType()))
                .header(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE, StrUtil.emptyIfNull(info.getDeviceType()))
                .header(HeaderConstants.HEADER_INTERNAL_ROLES, CollUtil.join(info.getRoles(), ","))
                .header(HeaderConstants.HEADER_INTERNAL_PERMS, CollUtil.join(info.getPerms(), ","))
                .build();
    }

}
