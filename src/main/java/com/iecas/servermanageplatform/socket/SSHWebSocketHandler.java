package com.iecas.servermanageplatform.socket;


import com.alibaba.fastjson2.JSON;
import com.github.lalyos.jfiglet.FigletFont;
import com.iecas.servermanageplatform.common.SocketMessage;
import com.iecas.servermanageplatform.pojo.enums.CharsetEnum;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.tomcat.websocket.WsSession;
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
@ServerEndpoint("/websocket/ssh")
public class SSHWebSocketHandler {


    private static ConcurrentHashMap<String, SSHWebSocketHandler> webSocketServerMap = new ConcurrentHashMap<>();
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
        // 建立socket连接
        this.sessionSocket = sessionSocket;

        // 获取链接参数
        Map<String, List<String>> requestParameterMap = sessionSocket.getRequestParameterMap();
        String host = requestParameterMap.get("host").get(0);
        int port = Integer.parseInt(requestParameterMap.get("port").get(0));
        String username = requestParameterMap.get("username").get(0);
        String password = requestParameterMap.get("password").get(0);

        sshClient = new SSHClient();
        try {
            // 设置连接最大超时时间
            sshClient.setConnectTimeout(600000);
            // 不验证主机密钥
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            // 通过用户名和密码登录目的主机
            sshClient.connect(host, port);
            sshClient.authPassword(username, password);
        } catch (IOException e){
            log.error("服务器建立连接异常! ", e);
            sendMessage(sessionSocket, "FailMessage", true,"Fail to connect remote server !");
            return;
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