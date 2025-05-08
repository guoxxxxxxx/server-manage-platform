package com.iecas.servermanageplatform.utils.serverDetails;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 16:03
 * @Description: 工厂类
 */
public class ServerDetailsFactory {


    public static ServerDetailsUtils create(String type){
        return switch (type.toLowerCase()){
            case "ubuntu" -> new UbuntuServerDetailsUtils();
            default -> throw new IllegalArgumentException("未知类型: " + type);
        };
    }
}
