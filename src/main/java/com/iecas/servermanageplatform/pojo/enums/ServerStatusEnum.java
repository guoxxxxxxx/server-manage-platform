package com.iecas.servermanageplatform.pojo.enums;

import lombok.Getter;

/**
 * @Author: guo_x
 * @Date: 2025/5/14 11:06
 * @Description: 服务器状态枚举
 */
@Getter
public enum ServerStatusEnum {

    ONLINE("在线"),
    OFFLINE("离线"),
    SHUTDOWN("60s内关机")
    ;

    private String status;

    ServerStatusEnum(String status){
        this.status = status;
    }
}
