package com.iecas.servermanageplatform.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iecas.servermanageplatform.entity.ServerRoleAuthorityInfo;
import com.iecas.servermanageplatform.service.ServerRoleAuthorityInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

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

