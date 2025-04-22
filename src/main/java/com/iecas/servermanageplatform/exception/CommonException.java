/**
 * @Time: 2024/9/14 17:16
 * @Author: guoxun
 * @File: CommonException
 * @Description: 通用异常
 */

package com.iecas.servermanageplatform.exception;


public class CommonException extends RuntimeException{

    public CommonException(String message){
        super(message);
    }
}
