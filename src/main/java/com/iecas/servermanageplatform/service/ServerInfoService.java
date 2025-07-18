package com.iecas.servermanageplatform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iecas.servermanageplatform.pojo.dto.QueryServerInfoDTO;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.vo.AddServerInfoVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<ServerInfo> getByIds(List<Long> ids);


    /**
     * 根据id列表更新服务器硬件信息
     * @param ids id列表
     * @return 更新结果
     */
    List<ServerInfo> updateHardwareInfoByIds(List<Long> ids);


    /**
     * 批量更新服务器硬件信息
     * @param ids id列表
     */
    void updateServerHardwareInfo(List<Long> ids);


    /**
     * 根据服务器id关闭服务器
     * @param serverId 服务器id
     * @return
     */
    boolean shutdownById(Long serverId);


    /**
     * 根据服务器id取消关闭服务器
     * @param serverId 服务器id
     * @return 取消关闭结果
     */
    boolean cancelShutdownById(Long serverId);


    /**
     * 根据服务器id重启服务器
     * @param serverId  服务器id
     * @return 重启结果
     */
    boolean rebootById(Long serverId);


    /**
     * 关闭所有服务器
     * @return 关闭结果
     */
    Map<String, Object> shutdownByIds(List<Long> serverIdList);


    /**
     * 取消所有服务器的关闭
     * @return 取消关闭结果
     */
    Map<String, Object> cancelShutdown(List<Long> serverIdList);


    /**
     * 根据id查询服务器信息 加密服务器密码
     * @param id id
     * @return 服务器信息
     */
    ServerInfo getByIdEncryPwd(Long id);


    /**
     * 根据id更新服务器信息
     * @param serverInfo 服务器信息
     * @return 更新结果
     */
    boolean updateServerInfoById(ServerInfo serverInfo);


    /**
     * 认证当前用户是否有权限访问当前服务器
     * @param serverId 服务器id
     * @param userId 用户id
     * @return 认证结果
     */
    boolean auth(Long serverId, Long userId);


    /**
     * 查询所有服务器信息
     * @return 服务器信息
     */
    List<ServerInfo> getWhiteList(boolean isWhite);


    /**
     * 添加服务器到白名单
     * @param id 服务器id
     * @return 添加结果
     */
    boolean addServer2White(Long id);


    /**
     * 移除服务器白名单
     * @param id 服务器id
     * @return 移除结果
     */
    boolean removeWhite(Long id);


    /**
     * 获取仪表盘信息
     * @return 仪表盘信息
     */
    Map<String, Object> getDashboardInfo();


    /**
     * 删除服务器信息
     * @param id 服务器id
     * @return 删除结果
     */
    boolean deleteById(Long id);
}

