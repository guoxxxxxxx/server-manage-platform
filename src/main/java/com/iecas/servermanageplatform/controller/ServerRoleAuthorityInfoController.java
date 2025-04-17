package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.service.ServerRoleAuthorityInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (ServerRoleAuthorityInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@RestController
@RequestMapping("serverRoleAuthorityInfo")
public class ServerRoleAuthorityInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private ServerRoleAuthorityInfoService serverRoleAuthorityInfoService;
}

