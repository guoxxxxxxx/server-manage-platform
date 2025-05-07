/**
 * @Time: 2024/9/14 17:16
 * @Author: guoxun
 * @File: CommonException
 * @Description: 通用异常
 */

package com.iecas.servermanageplatform.exception;


public class AuthException extends RuntimeException{

    public AuthException(String message){
        super(message);
    }
}
