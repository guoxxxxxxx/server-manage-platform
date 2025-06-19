package com.iecas.servermanageplatform.utils.serverDetails;

import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.enums.ServerInfoCommandEnum;
import com.iecas.servermanageplatform.utils.SSHUtils;
import com.iecas.servermanageplatform.utils.UnitConvertUtils;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.transport.TransportException;
import org.springframework.data.util.Pair;

import static java.lang.Thread.sleep;

/**
* @Author: guo_x
* @Date: 2025/5/7 9:56
* @Description: 服务器信息获取工具类
*/
@Slf4j
public class UbuntuServerDetailsUtils extends ServerDetailsUtils {


    public String getOS(){
        try {
            String result = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getOS());
            return result.replaceFirst("PRETTY_NAME=", "");
        } catch (TransportException e){
            log.error("获取操作系统指令运行异常", e);
        }
        return "UNKNOWN";
    }


    public String getCPUInfo(){
        try {
            String result = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getCPU());
            return result.replaceFirst("Model name:\\s+", "");
        } catch (TransportException e){
            log.error("获取CPU信息指令运行异常", e);
        }
        return "UNKNOWN";
    }


    public Pair<Long, Long> getMemInfo(){
        try {
            String exec = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getMEM());
            String[] split = exec.split("\\s+");
            long total = UnitConvertUtils.parseToKB(split[1]);
            long available = UnitConvertUtils.parseToKB(split[6]);
            return Pair.of(total, available);
        } catch (TransportException e){
            log.error("获取内存信息指令运行异常", e);
        }
        return Pair.of(0L, 0L);
    }


    public Pair<Long, Long> getDiskInfo(){
        try {
            String exec = sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getDISK());
            String[] split = exec.split("\n")[1].split("\\s+");
            long total = UnitConvertUtils.parseToKB(split[1]);
            long available = UnitConvertUtils.parseToKB(split[3]);
            return Pair.of(total, available);
        } catch (TransportException e){
            log.error("获取磁盘信息指令运行异常", e);
        }
        return Pair.of(0L, 0L);
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
    public boolean shutdown(String password, int delayTime) {
        String shutdownCommand = String.format(ServerInfoCommandEnum.UBUNTU.getSHUTDOWN(delayTime), password);
        log.info("延迟关机: {}分", delayTime);
        try {
            sshUtils.exec(shutdownCommand);
        } catch (TransportException e) {
            log.error("关机指令运行异常", e);
            return false;
        }
        return true;
    }


    @Override
    public boolean cancelShutDown() {
        try {
            sshUtils.exec(ServerInfoCommandEnum.UBUNTU.getCANCEL_SHUTDOWN());
            return true;
        } catch (TransportException e){
            log.error("取消关机指令运行异常", e);
        }
        return false;
    }


    @Override
    public boolean reboot(String password) {
        try {
            String rebootCommand = String.format(ServerInfoCommandEnum.UBUNTU.getREBOOT(), password);
            sshUtils.exec(rebootCommand);
        } catch (TransportException e){
            if (e.getMessage().contains("EOF")){
                return true;
            } else {
                log.error("重启指令异常", e);
            }
        }
        return false;
    }
}
