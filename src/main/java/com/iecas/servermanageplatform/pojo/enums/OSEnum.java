package com.iecas.servermanageplatform.pojo.enums;

import lombok.Getter;


/**
 * @Author: guo_x
 * @Date: 2025/5/9 9:59
 * @Description:
 */
@Getter
public enum OSEnum {

    UBUNTU("ubuntu");

    private String osName;


    OSEnum(String osName){
        this.osName = osName;
    }
}
