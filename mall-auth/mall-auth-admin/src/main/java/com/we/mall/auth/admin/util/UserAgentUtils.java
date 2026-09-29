package com.we.mall.auth.admin.util;

import cn.hutool.core.util.StrUtil;
import com.we.mall.common.core.enums.DeviceType;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * User-Agent 解析工具
 * <p>
 * 基于 Yauaa。
 *
 * @author we
 * @date 2026-09-29
 * @description
 */
public final class UserAgentUtils {

    private static volatile UserAgentAnalyzer UAA;

    private UserAgentUtils() {
    }

    /**
     * 启动时预加载
     */
    public static void init() {
        getUaa();
    }

    private static void getUaa() {
        if (UAA == null) {
            synchronized (UserAgentUtils.class) {
                if (UAA == null) {
                    UAA = UserAgentAnalyzer
                            .newBuilder()
                            .hideMatcherLoadStats()
                            .withCache(1000)
                            .withField("DeviceClass")
                            .withField("OperatingSystemName")
                            .withField("OperatingSystemNameVersion")
                            .withField("OperatingSystemNameVersion")
                            .withField("AgentNameVersion")
                            .withField("AgentName")
                            .withField("AgentVersion")
                            .build();
                }
            }
        }
    }

    /**
     * 解析设备类型
     */
    public static DeviceType parseDeviceType(String ua) {
        if (StrUtil.isBlank(ua)) {
            return DeviceType.UNKNOWN;
        }
        UserAgent agent = UAA.parse(ua);
        String deviceClass = agent.getValue("DeviceClass");
        if (deviceClass == null) {
            return DeviceType.UNKNOWN;
        }
        DeviceType type = DeviceType.of(deviceClass);
        return type == null ? DeviceType.UNKNOWN : type;
    }

    /**
     * 解析设备名称，如 "Chrome 88.0 on Windows 10"
     */
    public static String parseDeviceName(String ua) {
        if (StrUtil.isBlank(ua)) {
            return null;
        }
        UserAgent agent = UAA.parse(ua);

        String browser = friendlyBrowser(agent);
        String os = friendlyOs(agent);

        if (browser == null && os == null) {
            return null;
        }
        if (browser == null) {
            return os;
        }
        if (os == null) {
            return browser;
        }
        return browser + " on " + os;
    }


    private static String friendlyBrowser(UserAgent agent) {
        String name = clean(agent.getValue("AgentName"));
        String version = clean(agent.getValue("AgentVersion"));
        if (name == null) {
            return null;
        }
        return version == null ? name : name + " " + version;
    }

    private static String friendlyOs(UserAgent agent) {
        String name = clean(agent.getValue("OperatingSystemName"));
        String version = clean(agent.getValue("OperatingSystemVersion"));
        if (name == null) {
            return null;
        }
        // Windows NT → Windows
        name = name.replace("Windows NT", "Windows");
        // macOS → macOS（不变）
        if (version == null) {
            return name;
        }
        return name + " " + version;
    }

    private static String clean(String value) {
        if (StrUtil.isBlank(value) || "??".equals(value)) {
            return null;
        }
        return value;
    }

}
