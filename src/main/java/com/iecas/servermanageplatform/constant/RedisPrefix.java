/**
 * @Time: 2025/4/18 15:08
 * @Author: guoxun
 * @File: RedisPrefix
 * @Description:
 */

package com.iecas.servermanageplatform.constant;

public enum RedisPrefix {

    /**
     * 注册验证码
     */
    REGISTER_AUTH_CODE("auth:code:register");



    private final String PREFIX;


    RedisPrefix(String prefix){
        this.PREFIX = prefix;
    }

    public String getPREFIX(String key){
        return this.PREFIX + key;
    }


}
