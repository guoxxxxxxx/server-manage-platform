package com.iecas.servermanageplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecas.servermanageplatform.pojo.dto.ResetPasswordDTO;
import com.iecas.servermanageplatform.pojo.dto.UserLoginDTO;
import com.iecas.servermanageplatform.pojo.dto.UserRegisterDTO;
import com.iecas.servermanageplatform.pojo.dto.ValidAuthCodeDTO;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * (UserInfo)表服务接口
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



public interface UserInfoService extends IService<UserInfo> {


    /**
     * 用户注册
     * @param dto 注册信息
     */
    void register(UserRegisterDTO dto);


    /**
     * 获取验证码
     * @param email 注册邮箱
     * @param mode 验证码模式
     */
    void sendAuthCode(String email, String mode) throws Exception;


    /**
     * 登录
     * @param dto 登录信息
     * @return token
     */
    String login(UserLoginDTO dto, HttpServletRequest httpServletRequest);


    /**
     * 解析token
     * @param token token
     * @return UserInfo
     */
    UserInfo parseUserInfoByToken(String token);


    /**
     * 验证验证码
     * @param dto 验证码信息
     * @return true/false
     */
    boolean validAuthCode(ValidAuthCodeDTO dto);


    /**
     * 重置密码
     * @param dto 重置密码信息
     * @return true/false
     */
    boolean reset(ResetPasswordDTO dto);
}

