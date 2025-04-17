package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.service.ServerUserPasswordInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (ServerUserPasswordInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@RestController
@RequestMapping("serverUserPasswordInfo")
public class ServerUserPasswordInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private ServerUserPasswordInfoService serverUserPasswordInfoService;
}

