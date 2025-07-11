package com.iecas.servermanageplatform.pojo.enums;

import lombok.Getter;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 14:52
 * @Description: 获取服务器信息指令集
 */

@Getter
public enum ServerInfoCommandEnum {

    UBUNTU(
            "lscpu | grep -E \"Model name:|型号名称：\"",
            "cat /etc/os-release | grep PRETTY_NAME=",
            "free -k | grep -E \"Mem:|内存：\"",
            "df -k /home",
            "echo '%s' | sudo -S shutdown -h ",
            "shutdown -c",
            "echo '%s' | sudo -S reboot"
    );

    private String CPU;

    private String OS;

    private String DISK;

    private String MEM;

    private String SHUTDOWN;

    private String CANCEL_SHUTDOWN;

    private String REBOOT;

    ServerInfoCommandEnum(String CPU, String OS, String MEM, String DISK, String SHUTDOWN, String CANCEL_SHUTDOWN,
                          String REBOOT){
        this.CPU = CPU;
        this.OS = OS;
        this.MEM = MEM;
        this.DISK = DISK;
        this.SHUTDOWN = SHUTDOWN;
        this.CANCEL_SHUTDOWN = CANCEL_SHUTDOWN;
        this.REBOOT = REBOOT;
    }


    public String getSHUTDOWN(int delayTime){
        return SHUTDOWN + delayTime;
    }
}
