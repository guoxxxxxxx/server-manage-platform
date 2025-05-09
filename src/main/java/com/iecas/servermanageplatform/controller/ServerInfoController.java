package com.iecas.servermanageplatform.controller;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iecas.servermanageplatform.aop.annotation.Auth;
import com.iecas.servermanageplatform.aop.annotation.Logger;
import com.iecas.servermanageplatform.common.CommonResult;
import com.iecas.servermanageplatform.common.PageResult;
import com.iecas.servermanageplatform.pojo.dto.QueryServerInfoDTO;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.vo.AddServerInfoVO;
import com.iecas.servermanageplatform.service.ServerInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

/**
 * (ServerInfo)表控制层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@RestController
@RequestMapping("/serverInfo")
public class ServerInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private ServerInfoService serverInfoService;


    @Auth
    @Logger("添加服务器")
    @PostMapping("/addServers")
    public CommonResult addServers(@RequestBody List<ServerInfo> infoList){
        AddServerInfoVO vo = serverInfoService.addServers(infoList);
        return new CommonResult().success().data(vo);
    }


    @Auth
    @Logger("获取所有服务器列表")
    @GetMapping("/getPage")
    public CommonResult getPage(QueryServerInfoDTO dto){
        IPage<ServerInfo> result = serverInfoService.getPage(dto);
        return new CommonResult().data(new PageResult<>(result)).success();
    }


    @Auth
    @Logger("获取服务器详细信息")
    @GetMapping("/getServerInfo")
    public CommonResult getServerInfo(@RequestParam Integer id){
        ServerInfo serverInfo = serverInfoService.getById(id);
        return new CommonResult().data(serverInfo).success();
    }


    @Auth
    @Logger("根据服务器ids获取指定服务器的详细信息")
    @PostMapping("/getServerInfoByIds")
    public CommonResult getServerInfoByIds(@RequestBody List<Integer> ids){
        List<ServerInfo> result = serverInfoService.getByIds(ids);
        return new CommonResult().data(result).success();
    }
}

