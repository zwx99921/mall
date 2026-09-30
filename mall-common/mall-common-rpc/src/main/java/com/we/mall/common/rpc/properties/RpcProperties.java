package com.we.mall.common.rpc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 服务间通信配置
 *
 * @author we
 * @date 2026-09-28
 * @description
 */
@Data
@ConfigurationProperties(prefix = "mall.rpc")
public class RpcProperties {

    /**
     * 服务间调用凭证
     * <p>
     * 出站时带上，入站时校验。
     * 生产环境建议放配置中心。
     */
    private String serviceToken;

    /**
     * 需要服务凭证校验的内部接口路径，支持 Ant 风格
     */
    private List<String> internalPaths = new ArrayList<>(Arrays.asList("/inner/**", "/actuator/systemInfo"));

    /**
     * 启动时校验 serviceToken 必填
     */
    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(serviceToken)) {
            throw new IllegalStateException("mall.rpc.service-token 未配置，服务间调用无法进行");
        }
    }

}
