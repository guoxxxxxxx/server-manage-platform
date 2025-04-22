/**
 * @Time: 2025/4/22 13:42
 * @Author: guoxun
 * @File: IPUtils
 * @Description:
 */

package com.iecas.servermanageplatform.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IPUtils {


    /**
     * 获取客户端真实 IP 地址，考虑代理和负载均衡的情况
     */
    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 多个 IP 的情况，取第一个
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                } else {
                    return ip.trim();
                }
            }
        }

        // 如果没有通过代理，则直接获取
        return request.getRemoteAddr();
    }
}
