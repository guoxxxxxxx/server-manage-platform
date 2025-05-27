package com.iecas.servermanageplatform.socket;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lalyos.jfiglet.FigletFont;
import com.iecas.servermanageplatform.common.SocketMessage;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerUserPasswordInfo;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.pojo.enums.CharsetEnum;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.service.ServerUserPasswordInfoService;
import com.iecas.servermanageplatform.service.UserInfoService;
import com.iecas.servermanageplatform.utils.SpringContextUtils;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint("/websocket/autoAuthSsh")
public class AutoAuthSSHWebSocketHandler {


    private static ConcurrentHashMap<String, AutoAuthSSHWebSocketHandler> webSocketServerMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, SSHClient> sshClientMap = new ConcurrentHashMap<>();
    private Session sessionSocket;
    private SSHClient sshClient;
    private Charset serverCharset;
    private String sshKey;
    private InputStream shellInputStream;
    private OutputStream shellOutputStream;
    private Thread shellOutThread;
    private net.schmizz.sshj.connection.channel.direct.Session.Shell shell;


    @OnOpen
    public void onOpen(Session sessionSocket) throws IOException{
        // 获取实例对象
        UserInfoService userInfoService = SpringContextUtils.getBean(UserInfoService.class);
        ServerInfoService serverInfoService = SpringContextUtils.getBean(ServerInfoService.class);
        ServerUserPasswordInfoService serverUserPasswordInfoService = SpringContextUtils.getBean(ServerUserPasswordInfoService.class);

        // 建立socket连接
        this.sessionSocket = sessionSocket;

        // 获取用户token信息和所要链接服务器的id
        Map<String, List<String>> requestParameterMap = sessionSocket.getRequestParameterMap();
        String token = requestParameterMap.get("token").get(0);
        String serverId = requestParameterMap.get("serverId").get(0);

        // 解析用户token信息
        UserInfo userInfo = userInfoService.parseUserInfoByToken(token);

        // 判断当前用户是否有权限
        if (!serverInfoService.auth(Long.parseLong(serverId), userInfo.getId())){
            sendMessage(sessionSocket, "WarningMessage", true, "当前用户无权限");
            sendMessage(sessionSocket, "FailMessage", true, "当前用户无权限！请向管理员申请访问!");
            log.debug("当前用户无权限");
            return;
        }

        // 获取所要链接服务器的详细信息
        ServerInfo serverInfo = serverInfoService.getById(serverId);
        // 查询当前用户所存的当前服务器的用户名与密码
        List<ServerUserPasswordInfo> serverUserPasswordInfoList = serverUserPasswordInfoService.list(new LambdaQueryWrapper<ServerUserPasswordInfo>()
                .eq(ServerUserPasswordInfo::getServerId, serverId)
                .eq(ServerUserPasswordInfo::getUserId, userInfo.getId()));
        int usernamePasswordIndex = 0;

        // 与服务器建立连接
        String host = serverInfo.getIp();
        int port = Integer.parseInt(serverInfo.getPort());


        sshClient = new SSHClient();
        while (usernamePasswordIndex < serverUserPasswordInfoList.size()){
            String username = serverUserPasswordInfoList.get(usernamePasswordIndex).getUsername();
            String password = serverUserPasswordInfoList.get(usernamePasswordIndex).getPassword();
            usernamePasswordIndex += 1;
            try {
                // 设置连接最大超时时间
                sshClient.setConnectTimeout(600000);
                // 不验证主机密钥
                sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                // 通过用户名和密码登录目的主机
                sshClient.connect(host, port);
                try {
                    sshClient.authPassword(username, password);
                    break;
                } catch (IOException e){
                    log.debug("当前用户名密码验证失败,正在进行其他尝试...");
                }
            } catch (IOException e){
                log.error("服务器建立连接异常! ", e);
                sendMessage(sessionSocket, "FailMessage", true,"Fail to connect remote server !");
                return;
            }
            if (usernamePasswordIndex == serverUserPasswordInfoList.size()){
                sendMessage(sessionSocket, "FailMessage", true, "连接远程服务器失败, 请检查用户名和密码或服务器ip、端口配置!");
            }
        }

        // 获取服务器编码格式
        try (net.schmizz.sshj.connection.channel.direct.Session sshSession = sshClient.startSession();
             net.schmizz.sshj.connection.channel.direct.Session.Command command = sshSession.exec("echo $LANG")){
            String locale = IOUtils.readFully(command.getInputStream()).toString();
            command.join();
            serverCharset = Charset.forName(CharsetEnum.getByLinuxCharset(locale).getJavaCharset());
            sshClient.setRemoteCharset(serverCharset);
        }

        // 开启交互终端
        net.schmizz.sshj.connection.channel.direct.Session sshSession = sshClient.startSession();
        sshSession.allocateDefaultPTY();

        // 连接成功, 生成key标识
        sshKey = serverCharset.name().replace("-", "@") + "-" + UUID.randomUUID();
        sendMessage(sessionSocket, "SSHKey", true, sshKey);
        webSocketServerMap.put(sshKey, this);
        sshClientMap.put(sshKey, sshClient);

        // 欢迎语
        // sendMessage(sessionSocket, "ShellOut", true, "🚀欢迎使用本系统\r\nPower By FlashPipi\r\n");
        // 生成艺术字
        String titleArt = FigletFont.convertOneLine("iecas");
        String[] titleArtSplit = titleArt.split("\n");
        for (String artLine : titleArtSplit){
            sendMessage(sessionSocket, "ShellOut", true, artLine + "\r\n");
        }

        // 启动终端输出线程
        shell = sshSession.startShell();
        shellInputStream = shell.getInputStream();
        shellOutputStream = shell.getOutputStream();
        shellOutThread = new Thread(() -> {
            byte[] buffer = new byte[8192];
            int len;
            try {
                while ((len = shellInputStream.read(buffer)) != -1){
                    String shellOut = new String(buffer, 0, len, serverCharset);
                    sendMessage(sessionSocket, "ShellOut", true, shellOut);
                }
            } catch (IOException e){
                log.error("终端输出错误", e);
            }
        });
        shellOutThread.start();

    }


    @OnMessage
    public void onMessage(String message) throws IOException{
        if (shell == null || shellOutputStream == null) return;

        SocketMessage socketMessage = JSON.parseObject(message, SocketMessage.class);

        // 更改虚拟终端大小
        if (socketMessage.getTitle().equalsIgnoreCase("WINDOWS_CHANGE")){
            shell.changeWindowDimensions(socketMessage.getColSize(), socketMessage.getRowSize(), 0, 0);
        }

        // 文本命令
        if (socketMessage.getTitle().equalsIgnoreCase("USER_TEXT")){
            shellOutputStream.write(socketMessage.getMessage().getBytes(serverCharset));
            shellOutputStream.flush();
        }

        // 心跳续约
        if (socketMessage.getTitle().equalsIgnoreCase("HEART_BEAT")){
            shellOutputStream.write("".getBytes(serverCharset));
            shellOutputStream.flush();
        }
    }


    @OnClose
    public void onClose() throws IOException{

        String key = sshKey;
        // 关闭线程
        if (shellOutThread != null && !shellOutThread.isInterrupted()){
            shellOutThread.isInterrupted();
        }
        // 关闭输出流
        if (shellOutputStream != null){
            shellOutputStream.close();
        }
        // 关闭输入流
        if (shellInputStream != null){
            shellInputStream.close();
        }
        // 关闭终端
        if (shell != null){
            shell.close();
        }
        // 关闭连接
        if(webSocketServerMap.get(key) != null && webSocketServerMap.get(key).sessionSocket != null) {
            webSocketServerMap.get(key).sessionSocket.close();
        }
        webSocketServerMap.remove(key);
        sessionSocket = null;
        if (sshClientMap.get(key) != null){
            sshClientMap.get(key).close();
        }
        sshClientMap.remove(key);
        sshClient = null;
        sshKey = null;
    }


    @OnError
    public void onError(Session sessionSocket, Throwable error){
        log.error("Socket内部错误", error);
    }


    /**
     * 向客户端发送消息
     * @param sessionSocket socket连接
     * @param message 消息
     * @param isShow 是否显示
     */
    private void sendMessage(Session sessionSocket, String title, boolean isShow, String message){
        synchronized (sessionSocket) {
            try {
                if (isShow){
                    sessionSocket.getBasicRemote().sendText(JSON.toJSONString(SocketMessage.success(title, message)));
                }
                else {
                    sessionSocket.getBasicRemote().sendText(JSON.toJSONString(SocketMessage.fail(title, message)));
                }
            } catch (IOException e) {
                log.error("消息发送失败", e);
            }
        }
    }
}