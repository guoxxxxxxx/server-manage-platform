/**
 * @Time: 2025/4/23 14:47
 * @Author: guoxun
 * @File: AuthAspect
 * @Description:
 */

package com.iecas.servermanageplatform.aop.aspect;


import com.alibaba.fastjson2.JSON;
import com.iecas.servermanageplatform.aop.annotation.Logger;
import com.iecas.servermanageplatform.config.UserThreadLocal;
import com.iecas.servermanageplatform.exception.AuthException;
import com.iecas.servermanageplatform.exception.CustomLoginExpiredException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.service.UserInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Method;


@Aspect
@Slf4j
@Component
public class AuthAspect {

    @Resource
    private UserInfoService userInfoService;

    @Pointcut("@annotation(com.iecas.servermanageplatform.aop.annotation.Auth)")
    public void pointCut(){}


    @Before("pointCut()")
    public void doBefore(JoinPoint joinPoint) throws Exception{
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 从请求头部获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasLength(token)){
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasLength(bearerToken) && bearerToken.startsWith("Bearer ")){
                token = bearerToken.substring(7);
            }
        }
        if (!StringUtils.hasLength(token)){
            throw new CustomLoginExpiredException("token不存在, 请重新登录!");
        }

        UserInfo userInfo = userInfoService.parseUserInfoByToken(token);
        // 从数据库中查询用户信息，确保信息的实时性
        UserInfo currentUser = userInfoService.getById(userInfo.getId());
        // 判断当前用户是否被封禁
        if(currentUser.getLocked()){
            throw new WarningTipsException("当前用户已被封禁");
        }
        UserThreadLocal.setUserInfo(currentUser);
    }


    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void doAfter(JoinPoint joinPoint, Object result){
        UserThreadLocal.removeUser();
    }

}
