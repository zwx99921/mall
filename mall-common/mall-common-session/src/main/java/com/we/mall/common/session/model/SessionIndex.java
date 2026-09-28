package com.we.mall.common.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会话索引
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类型：PC / H5 / APP / MINI
     */
    private String deviceType;

    /**
     * 会话ID
     */
    private String sessionId;

}
