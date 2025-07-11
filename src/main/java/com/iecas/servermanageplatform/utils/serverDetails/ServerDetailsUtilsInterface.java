package com.iecas.servermanageplatform.utils.serverDetails;

import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import org.springframework.data.util.Pair;

/**
* @Author: guo_x
* @Date: 2025/5/7 15:50
* @Description: 
*/
public interface ServerDetailsUtilsInterface {

    /**
     * 获取服务器操作系统
     * @return 操作系统信息
     */
    String getOS();


    /**
     * 获取服务器CPU信息
     * @return CPU信息
     */
    String getCPUInfo();


    /**
     * 获取服务器内存信息
     * @return 内存信息 (总内存, 可用内存)
     */
    Pair<Long, Long> getMemInfo();


    /**
     * 获取服务器硬盘信息
     * @return 硬盘信息 (总硬盘, 可用硬盘)
     */
    Pair<Long, Long> getDiskInfo();


    /**
     * 获取服务器硬件信息
     * @return 服务器硬件信息
     */
    ServerHardwareInfo getServerHardwareInfo();


    /**
     * 关闭服务器
     * @param password 当前服务器管理员密码
     * @param delayTime 延迟时间
     * @return 关闭结果
     */
    boolean shutdown(String password, int delayTime);


    /**
     * 取消服务器关机
     * @return 取消结果
     */
    boolean cancelShutDown();


    /**
     * 重启服务器
     * @param password 当前服务器管理员密码
     * @return 重启结果
     */
    boolean reboot(String password);
}
