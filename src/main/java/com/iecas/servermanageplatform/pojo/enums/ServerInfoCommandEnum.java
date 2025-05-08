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
            "lscpu | grep \"Model name:\"",
            "cat /etc/os-release | grep PRETTY_NAME=",
            "free -h | grep Mem:",
            "df -h /"
    );

    private String CPU;

    private String OS;

    private String DISK;

    private String MEM;

    ServerInfoCommandEnum(String CPU, String OS, String MEM, String DISK){
        this.CPU = CPU;
        this.OS = OS;
        this.MEM = MEM;
        this.DISK = DISK;
    }
}
