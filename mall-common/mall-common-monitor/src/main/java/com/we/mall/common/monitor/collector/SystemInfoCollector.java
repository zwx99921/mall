package com.we.mall.common.monitor.collector;

import cn.hutool.core.date.DateUtil;
import com.sun.management.OperatingSystemMXBean;
import com.we.mall.common.monitor.model.response.SystemInfoResponse;
import com.we.mall.common.monitor.util.FileSizeUtils;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 系统信息采集器
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
public class SystemInfoCollector {

    private final String appName;
    private final String appVersion;
    private final long startMillis = System.currentTimeMillis();

    public SystemInfoCollector(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    // ==================== App ====================

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ==================== JVM ====================

    public SystemInfoResponse.AppInfo collectApp() {
        SystemInfoResponse.AppInfo app = new SystemInfoResponse.AppInfo();
        app.setName(appName);
        app.setVersion(appVersion);
        app.setStartTime(DateUtil.formatDateTime(DateUtil.date(startMillis)));
        app.setRunTime(DateUtil.formatBetween(System.currentTimeMillis() - startMillis));
        return app;
    }

    // ==================== CPU ====================

    public SystemInfoResponse.JvmInfo collectJvm() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        long max = runtime.maxMemory();

        SystemInfoResponse.JvmInfo jvm = new SystemInfoResponse.JvmInfo();
        jvm.setJavaVersion(System.getProperty("java.version"));
        jvm.setJavaHome(System.getProperty("java.home"));
        jvm.setTotalMemory(FileSizeUtils.format(total));
        jvm.setUsedMemory(FileSizeUtils.format(used));
        jvm.setFreeMemory(FileSizeUtils.format(free));
        jvm.setMaxMemory(FileSizeUtils.format(max));
        jvm.setUsagePercent(round((double) used / total * 100));
        return jvm;
    }

    // ==================== OS ====================

    public SystemInfoResponse.CpuInfo collectCpu() {
        SystemInfoResponse.CpuInfo cpu = new SystemInfoResponse.CpuInfo();
        cpu.setCores(Runtime.getRuntime().availableProcessors());

        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof OperatingSystemMXBean) {
            OperatingSystemMXBean sunOsBean = (OperatingSystemMXBean) osBean;
            double load = sunOsBean.getSystemCpuLoad();
            if (load >= 0) {
                cpu.setUsagePercent(round(load * 100));
            }
        }
        cpu.setLoadAverage(String.format("%.2f", osBean.getSystemLoadAverage()));
        return cpu;
    }

    // ==================== Disk ====================

    public SystemInfoResponse.OsInfo collectOs() {
        SystemInfoResponse.OsInfo os = new SystemInfoResponse.OsInfo();
        os.setName(System.getProperty("os.name"));
        os.setVersion(System.getProperty("os.version"));
        os.setArch(System.getProperty("os.arch"));
        return os;
    }

    // ==================== 私有 ====================

    public SystemInfoResponse.DiskInfo collectDisk() {
        File root = new File("/");
        long total = root.getTotalSpace();
        long free = root.getFreeSpace();
        long used = total - free;

        SystemInfoResponse.DiskInfo disk = new SystemInfoResponse.DiskInfo();
        disk.setTotal(FileSizeUtils.format(total));
        disk.setUsed(FileSizeUtils.format(used));
        disk.setFree(FileSizeUtils.format(free));
        disk.setUsagePercent(total > 0 ? round((double) used / total * 100) : 0.0);
        return disk;
    }

}
