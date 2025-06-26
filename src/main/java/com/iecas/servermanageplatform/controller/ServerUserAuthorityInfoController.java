package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.aop.annotation.Auth;
import com.iecas.servermanageplatform.aop.annotation.Logger;
import com.iecas.servermanageplatform.common.CommonResult;
import com.iecas.servermanageplatform.pojo.vo.UserAuthServerVO;
import com.iecas.servermanageplatform.service.ServerUserAuthorityInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (ServerUserAuthorityInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */
@RestController
@RequestMapping("/serverUserAuthorityInfo")
public class ServerUserAuthorityInfoController {


    /**
     * 服务对象
     */
    @Autowired
    private ServerUserAuthorityInfoService serverUserAuthorityInfoService;


    @Auth
    @Logger("获取当前服务器的用户访问权限表")
    @GetMapping("/getCurrentServerUserAuth/{id}")
    public CommonResult getCurrentServerUserAuth(@PathVariable(name = "id") Long id){
        List<UserAuthServerVO> result = serverUserAuthorityInfoService.getCurrentServerUserAuth(id);
        return new CommonResult().data(result).success();
    }


    @Auth
    @Logger("更改服务器对应的权限信息")
    @PutMapping("/updateUserAuthInfo/{serverId}/{userId}/{auth}")
    public CommonResult updateUserAuthInfo(@PathVariable(name = "serverId") Long serverId,
                                           @PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "auth") Boolean auth){
        boolean result = serverUserAuthorityInfoService.updateUserAuthInfo(serverId, userId, auth);
        return new CommonResult().data(result).success();
    }
}

