/**
 * @Time: 2024/8/30 16:53
 * @Author: guoxun
 * @File: GlobalExceptionHandler
 * @Description:
 */

package com.iecas.servermanageplatform.config;


import com.iecas.servermanageplatform.common.CommonResult;
import com.iecas.servermanageplatform.exception.CommonException;
import com.iecas.servermanageplatform.exception.LoginExpiredException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CommonGlobalExceptionHandler {


    /**
     * 全局异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e){
        log.error(e.toString());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", e.toString());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 公共异常, 向用户以错误信息形式展示message中的信息
     * @param e CommonException
     * @return
     */
    @ExceptionHandler(CommonException.class)
    public CommonResult handleCommonException(CommonException e){
        return new CommonResult().status(5403).message(e.getMessage());
    }


    /**
     * 公共异常, 向用户以警告形式展示message中的信息
     * @param e CommonException
     * @return
     */
    @ExceptionHandler(WarningTipsException.class)
    public CommonResult handleCommonException(WarningTipsException e){
        return new CommonResult().status(5400).message(e.getMessage());
    }


    /**
     * 登录过期异常
     * @param e LoginExpiredException
     * @return CommonResult
     */
    @ExceptionHandler(LoginExpiredException.class)
    public CommonResult handleLoginExpiredException(LoginExpiredException e){
        return new CommonResult().status(5503).message(e.getMessage());
    }

}
