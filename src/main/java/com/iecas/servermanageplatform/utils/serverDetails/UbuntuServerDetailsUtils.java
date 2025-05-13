package com.iecas.servermanageplatform.utils.serverDetails;

import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.enums.ServerInfoCommandEnum;
import com.iecas.servermanageplatform.utils.SSHUtils;
import com.iecas.servermanageplatform.utils.UnitConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

/**
* @Author: guo_x
* @Date: 2025/5/7 9:56
* @Description: 服务器信息获取工具类
*/
@Slf4j
public class UbuntuServerDetailsUtils extends ServerDetailsUtils {


    public String getOS(){
        String result = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getOS());
        return result.replaceFirst("PRETTY_NAME=", "");
    }


    public String getCPUInfo(){
        String result = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getCPU());
        return result.replaceFirst("Model name:\\s+", "");
    }


    public Pair<Long, Long> getMemInfo(){
        String exec = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getMEM());
        String[] split = exec.split("\\s+");
        long total = UnitConvertUtils.parseToKB(split[1]);
        long available = UnitConvertUtils.parseToKB(split[6]);
        return Pair.of(total, available);
    }


    public Pair<Long, Long> getDiskInfo(){
        String exec = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getDISK());
        String[] split = exec.split("\n")[1].split("\\s+");
        long total = UnitConvertUtils.parseToKB(split[1]);
        long available = UnitConvertUtils.parseToKB(split[3]);
        return Pair.of(total, available);
    }


    @Override
    public ServerHardwareInfo getServerHardwareInfo() {
        String cpuInfo = getCPUInfo();
        String osInfo = getOS();
        Pair<Long, Long> diskInfo = getDiskInfo();
        Pair<Long, Long> memInfo = getMemInfo();
        ServerHardwareInfo hardwareInfo = ServerHardwareInfo.builder()
                .os(osInfo)
                .cpu(cpuInfo)
                .totalDiskSpace(diskInfo.getFirst())
                .freeDiskSpace(diskInfo.getSecond())
                .totalMemSpace(memInfo.getFirst())
                .freeMemSpace(memInfo.getSecond()).build();
        return hardwareInfo;
    }


    @Override
    public boolean shutdown(String password) {
        String shutdownCommand = String.format(ServerInfoCommandEnum.UBUNTU.getSHUTDOWN(), password);
        String exec = sshUtils.exec(shutdownCommand);
        log.info(exec);
        return true;
    }
}
