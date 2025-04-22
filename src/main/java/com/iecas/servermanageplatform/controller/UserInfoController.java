package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.aop.annotation.Logger;
import com.iecas.servermanageplatform.common.CommonResult;
import com.iecas.servermanageplatform.pojo.dto.UserRegisterDTO;
import com.iecas.servermanageplatform.service.UserInfoService;
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

}

