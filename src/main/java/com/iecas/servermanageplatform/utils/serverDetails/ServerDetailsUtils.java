package com.iecas.servermanageplatform.utils.serverDetails;

import com.iecas.servermanageplatform.utils.SSHUtils;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 15:45
 * @Description:
 */
public abstract class ServerDetailsUtils implements ServerDetailsUtilsInterface{

    // 当前服务器ssh客户端
    SSHUtils sshUtils;

    /**
     * 连接服务器
     * @param host 主机地址
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return true:连接成功 false:连接失败
     */
    public boolean connect(String host, int port, String username, String password){
        sshUtils = new SSHUtils(host, port, username, password);
        return sshUtils.connect();
    }


    /**
     * 连接服务器
     * @param host 主机地址
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return true:连接成功 false:连接失败
     */
    public boolean connect(String host, String port, String username, String password){
        sshUtils = new SSHUtils(host, Integer.parseInt(port), username, password);
        return sshUtils.connect();
    }


    /**
     * 获取当前服务器ssh客户端
     * @return ssh客户端
     */
    public SSHUtils getSshUtils(){
        return this.sshUtils;
    }
}
