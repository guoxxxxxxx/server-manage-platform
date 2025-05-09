package com.iecas.servermanageplatform.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.enums.OSEnum;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsFactory;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * @Author: guo_x
 * @Date: 2025/5/9 9:46
 * @Description: 更新服务器信息线程
 */
@Component
public class UpdateServerInfoTask {


    public static final UpdateServerInfoTask updateThread = new UpdateServerInfoTask();


    @Resource
    ServerInfoService serverInfoService;


    public void run() {
        // 查询所有服务器信息
        List<ServerInfo> serverInfoList = serverInfoService.list();
        // 遍历更新每一个服务器的信息
        for (ServerInfo e : serverInfoList){
            // 当前操作系统的指令集对象
            ServerDetailsUtils serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);
            // 判断当前服务器的操作系统
            // TODO 此处需要根据操作系统创建对应的指令集对象 当前默认采用ubuntu
            boolean connect = serverDetailsUtils.connect(e.getIp(), e.getPort(), e.getLoginUsername(), e.getLoginPassword());
            if (connect) {
                ServerHardwareInfo serverHardwareInfo = serverDetailsUtils.getServerHardwareInfo();
                // 将信息更新至数据库
                serverInfoService.update(new LambdaUpdateWrapper<ServerInfo>()
                        .eq(ServerInfo::getId, e.getId())
                        .set(ServerInfo::getCpu, serverHardwareInfo.getCpu())
                        .set(ServerInfo::getOperatingSystem, serverHardwareInfo.getOs())
                        .set(ServerInfo::getDiskSpace, serverHardwareInfo.getTotalDiskSpace())
                        .set(ServerInfo::getFreeDiskSpace, serverHardwareInfo.getFreeDiskSpace())
                        .set(ServerInfo::getMemorySpace, serverHardwareInfo.getTotalMemSpace())
                        .set(ServerInfo::getFreeMemorySpace, serverHardwareInfo.getFreeMemSpace())
                        .set(ServerInfo::getLastUpdate, new Date())
                        .set(ServerInfo::getStatus, "在线")
                );
            }
            else {
                serverInfoService.update(new LambdaUpdateWrapper<ServerInfo>()
                        .eq(ServerInfo::getId, e.getId())
                        .set(ServerInfo::getStatus, "离线")
                        .set(ServerInfo::getLastUpdate, new Date()));
            }
        }
    }
}
