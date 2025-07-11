package com.iecas.servermanageplatform.utils;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import java.io.IOException;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 10:30
 * @Description: ssh服务器连接工具
 */
@Slf4j
public class SSHUtils {

    // ssh客户端
    private SSHClient sshClient;
    // 主机ip
    private String host;
    // 端口号
    private int port;
    // 用户名
    private String username;
    // 密码
    private String password;


    public SSHUtils(String host, int port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * 连接服务器
     * @return true:连接成功 false:连接失败
     */
    public boolean connect() throws UserAuthException {
        this.sshClient = new SSHClient();
        try {
            // 设置最大连接超时时间
            sshClient.setConnectTimeout(1000);
            // 不验证主机密钥
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            // 通过用户名和密码登录用户主机
            sshClient.connect(host, port);
            sshClient.authPassword(username, password);
            return true;
        } catch (UserAuthException userAuthException){
            throw userAuthException;
        }
        catch (IOException e){
            log.debug("服务器建立连接异常");
            return false;
        }
    }


    /**
     * 执行命令
     * @param command 命令
     * @return 执行结果
     */
    public String exec(String command) throws TransportException {
        try(Session sshSession = sshClient.startSession();
            Session.Command result = sshSession.exec(command)){
            String res = IOUtils.readFully(result.getInputStream()).toString();
            return res;
        } catch (TransportException transportException){
            throw transportException;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 判断服务器是否存活
     * @return true:存活 false:不存活
     */
    public boolean isAlive(){
        return sshClient.isConnected();
    }
}
