package com.iecas.servermanageplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecas.servermanageplatform.pojo.dto.UserRegisterDTO;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;

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
}

