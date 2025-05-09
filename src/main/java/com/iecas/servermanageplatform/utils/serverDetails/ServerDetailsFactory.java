package com.iecas.servermanageplatform.utils.serverDetails;

import com.iecas.servermanageplatform.pojo.enums.OSEnum;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 16:03
 * @Description: 工厂类
 */
public class ServerDetailsFactory {


    public static ServerDetailsUtils create(OSEnum os){
        return switch (os.getOsName().toLowerCase()){
            case "ubuntu" -> new UbuntuServerDetailsUtils();
            default -> throw new IllegalArgumentException("未知类型: " + os.getOsName());
        };
    }
}
