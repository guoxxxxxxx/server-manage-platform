package com.iecas.servermanageplatform.utils.serverDetails;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @Author: guo_x
 * @Date: 2025/5/14 10:07
 * @Description:
 */
public class ServerOnlineChecker {


    /**
     * 检查指定IP和端口是否开放
     * @param ip ip
     * @param port 端口
     * @param timeoutMillis 超时时间
     * @return true表示端口开放，false表示端口未开放
     */
    public static boolean isPortOpen(String ip, int port, int timeoutMillis) {
        try (Socket socket = new Socket()) {
            SocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, timeoutMillis);  // 尝试连接
            return true;  // 连接成功，端口在线
        } catch (IOException e) {
            return false; // 连接失败，端口不通
        }
    }


    /**
     * 检查指定IP和端口是否开放
     * @param ip ip
     * @param sPort 端口
     * @param timeoutMillis 超时时间
     * @return true表示端口开放，false表示端口未开放
     */
    public static boolean isPortOpen(String ip, String sPort, int timeoutMillis) {
        int port = Integer.parseInt(sPort);
        try (Socket socket = new Socket()) {
            SocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, timeoutMillis);  // 尝试连接
            return true;  // 连接成功，端口在线
        } catch (IOException e) {
            return false; // 连接失败，端口不通
        }
    }
}
