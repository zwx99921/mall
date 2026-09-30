package com.we.mall.common.monitor.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统信息响应
 *
 * @author we
 * @date 2026-09-30
 * @description
 */
@Data
public class SystemInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private AppInfo app;
    private JvmInfo jvm;
    private CpuInfo cpu;
    private OsInfo os;
    private DiskInfo disk;

    @Data
    public static class AppInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String version;
        private String startTime;
        private String runTime;
    }

    @Data
    public static class JvmInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String javaVersion;
        private String javaHome;
        private String totalMemory;
        private String usedMemory;
        private String freeMemory;
        private String maxMemory;
        private Double usagePercent;
    }

    @Data
    public static class CpuInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer cores;
        private Double usagePercent;
        private String loadAverage;
    }

    @Data
    public static class OsInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String version;
        private String arch;
    }

    @Data
    public static class DiskInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String total;
        private String used;
        private String free;
        private Double usagePercent;
    }

}