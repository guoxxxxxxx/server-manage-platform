package com.iecas.servermanageplatform.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iecas.servermanageplatform.entity.ServerInfo;
import com.iecas.servermanageplatform.service.ServerInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * (ServerInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@RestController
@RequestMapping("serverInfo")
public class ServerInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private ServerInfoService serverInfoService;
}

