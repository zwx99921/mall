package com.we.mall.common.web.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * IP 工具类
 * 用于从请求中解析客户端真实 IP
 *
 * @author we
 * @date 2026-09-21
 * @description
 */
public final class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    /**
     * 常见代理请求头，按优先级排列
     */
    private static final List<String> IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    );

    private IpUtils() {
    }

    /**
     * 获取客户端真实 IP
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtils.getRequest());
    }

    /**
     * 获取客户端真实 IP
     * 注意：此方法会从右往左过滤掉可信代理 IP，取第一个非可信 IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // 1. 依次尝试代理头
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (isValid(ip)) {
                String realIp = parseRealIp(ip);
                if (realIp != null) {
                    return normalize(realIp);
                }
            }
        }

        // 2. 兜底使用 RemoteAddr
        return normalize(request.getRemoteAddr());
    }

    /**
     * 从 X-Forwarded-For 这类可能含多个 IP 的串里，解析出真实客户端 IP
     * 规则：从右往左，跳过可信代理，取第一个非可信 IP
     */
    private static String parseRealIp(String ipStr) {
        // 去掉端口，例如 1.2.3.4:5678
        String[] parts = ipStr.split(",");
        for (int i = parts.length - 1; i >= 0; i--) {
            String ip = parts[i].trim();
            if (!isValid(ip)) {
                continue;
            }
            ip = stripPort(ip);
            if (!isTrustedProxy(ip)) {
                return ip;
            }
        }
        // 全是可信代理时，返回最左边那个
        return stripPort(parts[0].trim());
    }

    /**
     * 是否可信代理 IP
     * 这里只放最基础的内网判断，生产环境建议改成从配置读取可信代理列表
     */
    private static boolean isTrustedProxy(String ip) {
        return isInternalIp(ip);
    }

    /**
     * 判断是否为内网 IP（IPv4）
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            return true;
        }
        byte[] addr = textToNumericFormatV4(ip);
        if (addr == null) {
            return false;
        }
        int b0 = addr[0] & 0xFF;
        int b1 = addr[1] & 0xFF;
        // 10.0.0.0/8
        if (b0 == 10) {
            return true;
        }
        // 172.16.0.0/12
        if (b0 == 172 && b1 >= 16 && b1 <= 31) {
            return true;
        }
        // 192.168.0.0/16
        if (b0 == 192 && b1 == 168) {
            return true;
        }
        // 169.254.0.0/16
        return b0 == 169 && b1 == 254;
    }

    /**
     * 判断是否为合法 IP 字符串
     */
    private static boolean isValid(String ip) {
        return ip != null
                && !ip.isEmpty()
                && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 去掉 IP 后面的端口，如 1.2.3.4:5678 -> 1.2.3.4
     * 兼容 IPv6 的 [::1]:8080 写法
     */
    private static String stripPort(String ip) {
        if (ip == null) {
            return null;
        }
        if (ip.startsWith("[")) {
            int end = ip.indexOf(']');
            return end > 0 ? ip.substring(1, end) : ip;
        }
        int idx = ip.lastIndexOf(':');
        // IPv4:port 情况，冒号后面是纯数字
        if (idx > 0 && idx == ip.lastIndexOf(':') && ip.indexOf(':') == idx) {
            String port = ip.substring(idx + 1);
            if (port.matches("\\d+")) {
                return ip.substring(0, idx);
            }
        }
        return ip;
    }

    /**
     * 归一化：本机 IPv6 转 IPv4 写法
     */
    private static String normalize(String ip) {
        if (ip == null || ip.isEmpty()) {
            return UNKNOWN;
        }
        if (LOCALHOST_IPV6.equals(ip)) {
            return LOCALHOST_IPV4;
        }
        return ip;
    }

    /**
     * IPv4 字符串转字节数组，非法返回 null
     */
    private static byte[] textToNumericFormatV4(String text) {
        String[] parts = text.split("\\.");
        if (parts.length != 4) {
            return null;
        }
        byte[] bytes = new byte[4];
        try {
            for (int i = 0; i < 4; i++) {
                int v = Integer.parseInt(parts[i]);
                if (v < 0 || v > 255) {
                    return null;
                }
                bytes[i] = (byte) v;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }

    /**
     * 获取本机 IP（用于服务注册、日志等）
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return LOCALHOST_IPV4;
        }
    }


}
