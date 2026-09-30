package com.we.mall.modules.monitor.config;

import com.we.mall.modules.monitor.properties.MonitorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 监控配置
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Configuration
@EnableConfigurationProperties(MonitorProperties.class)
public class MonitorConfig {

    @Bean
    public RestTemplate restTemplate(MonitorProperties monitorProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(monitorProperties.getFetchTimeoutMs());
        factory.setReadTimeout(monitorProperties.getFetchTimeoutMs());
        return new RestTemplate(factory);
    }

    /**
     * 监控线程池
     * <p>
     * 用于并发拉取服务实例信息。
     */
    @Bean("monitorExecutor")
    public ThreadPoolTaskExecutor monitorExecutor(MonitorProperties monitorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(monitorProperties.getFetchThreadPoolSize());
        // 最大线程数
        executor.setMaxPoolSize(monitorProperties.getFetchThreadPoolSize() * 2);
        // 队列容量
        executor.setQueueCapacity(monitorProperties.getFetchQueueCapacity());
        // 线程名前缀
        executor.setThreadNamePrefix("monitor-");
        // 拒绝策略：由调用线程执行（保证不丢任务）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 空闲线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        // 关闭时等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时长（秒）
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();
        return executor;
    }

}
