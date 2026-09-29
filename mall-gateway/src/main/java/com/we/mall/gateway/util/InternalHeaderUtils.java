package com.we.mall.gateway.util;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.session.model.SessionInfo;
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

    public static ServerHttpRequest buildRequest(ServerHttpRequest request, SessionInfo sessionInfo) {
        return request.mutate()
                .headers(headers -> {
                    // 清空所有外部可能传入的 X-Internal-*
                    headers.keySet().removeIf(k -> k.toLowerCase().startsWith(HeaderConstants.INTERNAL_HEADER_PREFIX.toLowerCase()));
                    // 设置请求头
                    headers.add(HeaderConstants.HEADER_INTERNAL_SESSION_ID, StrUtil.emptyIfNull(sessionInfo.getSessionId()));
                    headers.add(HeaderConstants.HEADER_INTERNAL_USER_ID, String.valueOf(sessionInfo.getUserId()));
                    headers.add(HeaderConstants.HEADER_INTERNAL_CLIENT_TYPE, StrUtil.emptyIfNull(sessionInfo.getClientType()));
                    headers.add(HeaderConstants.HEADER_INTERNAL_DEVICE_TYPE, StrUtil.emptyIfNull(sessionInfo.getDeviceType()));
                })
                .build();
    }

}
