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


    /**
     * 分页查询
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<ServerInfo> getPage(QueryServerInfoDTO dto);


    /**
     * 更新服务器硬件信息
     * @param serverId 服务器id
     * @return 更新结果
     */
    boolean updateHardwareInfo(Long serverId);


    /**
     * 根据id列表查询服务器的详细信息
     * @param ids id列表
     * @return 服务器列表
     */
    List<ServerInfo> getByIds(List<Integer> ids);
}

