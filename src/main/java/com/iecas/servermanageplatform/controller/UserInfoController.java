package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.aop.annotation.Logger;
import com.iecas.servermanageplatform.common.CommonResult;
import com.iecas.servermanageplatform.pojo.dto.ResetPasswordDTO;
import com.iecas.servermanageplatform.pojo.dto.UserLoginDTO;
import com.iecas.servermanageplatform.pojo.dto.UserRegisterDTO;
import com.iecas.servermanageplatform.pojo.dto.ValidAuthCodeDTO;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (UserInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@RestController
@RequestMapping("/user")
public class UserInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private UserInfoService userInfoService;


    @GetMapping("/sendAuthCode")
    @Logger("获取验证码")
    public CommonResult sendAuthCode(String email, String mode) throws Exception {
        userInfoService.sendAuthCode(email, mode);
        return new CommonResult().success().message("验证码发送成功");
    }


    @PostMapping("/register")
    @Logger("用户注册接口")
    public CommonResult register(@RequestBody UserRegisterDTO dto){
        userInfoService.register(dto);
        return new CommonResult().success();
    }


    @PostMapping("/login")
    @Logger("用户登录")
    public CommonResult login(@RequestBody UserLoginDTO dto, HttpServletRequest httpServletRequest){
        String token = userInfoService.login(dto, httpServletRequest);
        return new CommonResult().success().data(token);
    }


    @PostMapping("/parseUserInfoByToken")
    @Logger("通过token解析用户信息")
    public CommonResult parseUserInfoByToken(@RequestBody String token){
        UserInfo result = userInfoService.parseUserInfoByToken(token);
        return new CommonResult().success().data(result);
    }


    @PostMapping("/validAuthCode")
    @Logger("验证验证码是否正确")
    public CommonResult validAuthCode(@RequestBody ValidAuthCodeDTO dto){
        boolean result = userInfoService.validAuthCode(dto);
        return new CommonResult().data(result).success();
    }


    @PostMapping("/reset")
    @Logger("重置密码")
    public CommonResult reset(@RequestBody ResetPasswordDTO dto){
        boolean result = userInfoService.reset(dto);
        return new CommonResult().success().data(result);
    }

}

