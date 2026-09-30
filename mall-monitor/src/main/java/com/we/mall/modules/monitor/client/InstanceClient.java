package com.we.mall.modules.monitor.client;

import com.we.mall.common.core.constant.HeaderConstants;
import com.we.mall.common.monitor.constant.MonitorConstants;
import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import com.we.mall.common.rpc.properties.RpcProperties;
import com.we.mall.modules.monitor.model.response.ServiceInstanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 实例调用客户端
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InstanceClient {

    private final RestTemplate restTemplate;
    private final RpcProperties rpcProperties;

    /**
     * 拉取某个实例的系统信息
     */
    public ServiceInstanceResponse fetchSystemInfo(ServiceInstance instance) {
        try {
            String url = instance.getUri() + MonitorConstants.ACTUATOR_SYSTEM_INFO_PATH;

            HttpHeaders headers = new HttpHeaders();
            headers.set(HeaderConstants.HEADER_INTERNAL_SERVICE_TOKEN,
                    rpcProperties.getServiceToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SystemInfoResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, SystemInfoResponse.class);

            SystemInfoResponse systemInfo = response.getBody();
            if (systemInfo == null) {
                return null;
            }

            ServiceInstanceResponse info = new ServiceInstanceResponse();
            info.setServiceName(instance.getServiceId());
            info.setInstanceId(instance.getInstanceId());
            info.setHost(instance.getHost());
            info.setPort(instance.getPort());
            info.setSystemInfo(systemInfo);
            return info;

        } catch (Exception e) {
            log.warn("拉取实例信息失败: {}", instance.getUri(), e);
            return null;
        }
    }

}
