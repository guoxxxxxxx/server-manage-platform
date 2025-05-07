package com.iecas.servermanageplatform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iecas.servermanageplatform.pojo.dto.QueryServerInfoDTO;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.vo.AddServerInfoVO;

import java.util.List;

/**
 * (ServerInfo)表服务接口
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



public interface ServerInfoService extends IService<ServerInfo> {


    /**
     * 批量添加服务器信息
     * @param infoList 服务器信息
     * @return 添加结果
     */
    AddServerInfoVO addServers(List<ServerInfo> infoList);

    IPage<ServerInfo> getPage(QueryServerInfoDTO dto);
}

