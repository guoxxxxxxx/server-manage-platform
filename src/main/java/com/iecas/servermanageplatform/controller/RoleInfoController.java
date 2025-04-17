package com.iecas.servermanageplatform.controller;



import com.iecas.servermanageplatform.service.RoleInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (RoleInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:15
 */



@RestController
@RequestMapping("roleInfo")
public class RoleInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private RoleInfoService roleInfoService;
}

