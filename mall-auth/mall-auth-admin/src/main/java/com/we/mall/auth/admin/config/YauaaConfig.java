package com.we.mall.auth.admin.config;

import com.we.mall.auth.admin.util.UserAgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Yauaa User-Agent 解析配置
 * <p>
 * 启动时预加载，避免首次登录懒加载耗时。
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Slf4j
@Configuration
public class YauaaConfig {

    @PostConstruct
    public void init() {
        long start = System.currentTimeMillis();
        log.info("预加载 UserAgentUtils ...");
        UserAgentUtils.init();
        log.info("UserAgentUtils 预加载完成，耗时 {} ms", System.currentTimeMillis() - start);
    }
}
