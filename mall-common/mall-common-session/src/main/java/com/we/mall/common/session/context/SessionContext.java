package com.we.mall.common.session.context;

import com.we.mall.common.session.model.SessionInfo;
import com.we.mall.common.session.model.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 安全上下文
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private SessionInfo session;

    private SessionUser user;

}
