package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.config.UserThreadLocal;
import com.iecas.servermanageplatform.dao.ServerInfoDao;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.dto.QueryServerInfoDTO;
import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerUserPasswordInfo;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.pojo.enums.OSEnum;
import com.iecas.servermanageplatform.pojo.vo.AddServerInfoVO;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.service.ServerUserPasswordInfoService;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsFactory;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * (ServerInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Service("serverInfoService")
public class ServerInfoServiceImpl extends ServiceImpl<ServerInfoDao, ServerInfo> implements ServerInfoService {

    // 连接缓存
    private final ConcurrentHashMap<Long, ServerDetailsUtils> serverDetailsUtilsConcurrentHashMap = new ConcurrentHashMap<>();


    @Resource
    private ServerUserPasswordInfoService serverUserPasswordInfoService;


    @Override
    public AddServerInfoVO addServers(List<ServerInfo> infoList) {
        if (infoList == null || infoList.isEmpty()){
            throw new WarningTipsException("请添加信息");
        }

        // 从ThreadLocal中拿到当前用户信息
        UserInfo currentUser = UserThreadLocal.getUserInfo();

        List<ServerInfo> failList = new ArrayList<>();
        int successCount = 0, failCount = 0;

        // 遍历所有服务器信息
        for(ServerInfo serverInfo : infoList){
            // 判断当前服务器信息是否有效, 即判断ip是否有效
            if (!serverInfo.getIp().matches("^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$")){
                failCount += 1;
                failList.add(serverInfo);
                continue;
            }
            // 判断当前信息是否已经被存入过 根据主机ip来进行判断, 当二者都相等时, 则认为当前机器已经被添加过
            ServerInfo existServerInfo = baseMapper.selectOne(new LambdaQueryWrapper<ServerInfo>()
                    .eq(ServerInfo::getIp, serverInfo.getIp()));
            if (existServerInfo == null){
                // 更新用户信息
                serverInfo.setUserId(currentUser.getId());
                // 数据库中未检索到记录, 将记录保存至数据库
                int insert = baseMapper.insert(serverInfo);
                // 将当前用户所输入的用户名和密码存入tb_server_user_password数据库中
                ServerUserPasswordInfo serverUserPasswordInfo = ServerUserPasswordInfo.builder()
                        .serverId(serverInfo.getId())
                        .username(serverInfo.getLoginUsername())
                        .password(serverInfo.getLoginPassword())
                        .userId(currentUser.getId()).build();
                serverUserPasswordInfoService.save(serverUserPasswordInfo);
                successCount += insert;
                // 多线程更新服务器硬件信息
                Thread updateThread = new Thread(() -> {
                    updateHardwareInfo(serverInfo.getId());
                });
                updateThread.start();
            }
            else{
                // 查询当前用户针对当前服务器是否有存在的账户密码
                List<ServerUserPasswordInfo> serverUserPasswordInfoList = serverUserPasswordInfoService.list(new LambdaQueryWrapper<ServerUserPasswordInfo>()
                        .eq(ServerUserPasswordInfo::getUserId, currentUser.getId())
                        .eq(ServerUserPasswordInfo::getServerId, existServerInfo.getId()));

                // 如果不存在则直接插入
                if (serverUserPasswordInfoList == null || serverUserPasswordInfoList.isEmpty()){
                    ServerUserPasswordInfo serverUserPasswordInfo = ServerUserPasswordInfo.builder()
                            .userId(currentUser.getId())
                            .serverId(serverInfo.getId())
                            .password(serverInfo.getLoginPassword())
                            .username(serverInfo.getLoginUsername())
                            .serverId(existServerInfo.getId())
                            .build();
                    serverUserPasswordInfoService.save(serverUserPasswordInfo);
                }
                // 否则按错误处理
                else {
                    failCount += 1;
                    failList.add(serverInfo);
                }

            }
        }

        return AddServerInfoVO.builder()
                .allSuccess(failCount == 0)
                .failCount(failCount)
                .successCount(successCount)
                .failServerInfoList(failList).build();
    }


    @Override
    public IPage<ServerInfo> getPage(QueryServerInfoDTO dto) {
        return baseMapper.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), null);
    }


    /**
     * 更新服务器硬件信息
     * @param serverId 服务器id
     * @return
     */
    @Override
    public boolean updateHardwareInfo(Long serverId){
        // 查询服务器详细信息
        ServerInfo serverInfo = baseMapper.selectById(serverId);

        // 获取服务器硬件信息工具类 默认为ubuntu系统
        ServerDetailsUtils serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);

        // 判断当前操作系统并获得对应的对象实体
        String currentOS = serverInfo.getOperatingSystem();
        if (currentOS != null && currentOS.toLowerCase().contains("ubuntu")){
            serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);
        }

        // 连接ssh
        serverDetailsUtils.connect(serverInfo.getIp(), Integer.parseInt(serverInfo.getPort()),
                serverInfo.getLoginUsername(), serverInfo.getLoginPassword());
        // 获取硬件信息
        ServerHardwareInfo serverHardwareInfo = serverDetailsUtils.getServerHardwareInfo();
        // 将信息进行更新
        serverInfo.setCpu(serverHardwareInfo.getCpu());
        serverInfo.setOperatingSystem(serverHardwareInfo.getOs());
        serverInfo.setDiskSpace(serverHardwareInfo.getTotalDiskSpace());
        serverInfo.setFreeDiskSpace(serverHardwareInfo.getFreeDiskSpace());
        serverInfo.setMemorySpace(serverHardwareInfo.getTotalMemSpace());
        serverInfo.setFreeMemorySpace(serverHardwareInfo.getFreeMemSpace());
        serverInfo.setLastUpdate(new Date());
        baseMapper.updateById(serverInfo);
        return true;
    }


    @Override
    public List<ServerInfo> getByIds(List<Long> ids) {
        return baseMapper.selectList(new LambdaQueryWrapper<ServerInfo>()
                .in(ServerInfo::getId, ids));
    }


    @Override
    public List<ServerInfo> updateHardwareInfoByIds(List<Long> ids) {
        // 更新服务器信息
        updateServerHardwareInfo(ids);
        // 获取更新后的服务器信息
        return baseMapper.selectList(new LambdaQueryWrapper<ServerInfo>()
                .in(ServerInfo::getId, ids));
    }


    /**
     * 更新服务器信息
     * @param ids 指定的服务器id
     */
    @Override
    public void updateServerHardwareInfo(List<Long> ids) {
        // 将要更新的服务器信息
        List<ServerInfo> serverInfoList;
        if (ids == null || ids.isEmpty()) {
            // 查询所有服务器信息
            serverInfoList = baseMapper.selectList(null);
        }
        else{
            // 查询指定的服务器信息
            serverInfoList = baseMapper.selectList(new LambdaQueryWrapper<ServerInfo>()
                    .in(ServerInfo::getId, ids));
        }

        // 线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Void>> futures = new ArrayList<>();

        // 遍历更新每一个服务器的信息
        for (ServerInfo e : serverInfoList){
            futures.add(executorService.submit(() -> {
                updateSingleServerInfo(e);
                return null;
            }));
        }

        // 等待全部任务执行完
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
    }


    /**
     * 更新单个服务器信息
     * @param e 更新的服务器信息
     */
    private void updateSingleServerInfo(ServerInfo e){
        // 当前操作系统的指令集对象
        ServerDetailsUtils serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);

        // 是否链接成功
        boolean connect;

        // 判断缓存中是否有当前对象的链接
        if (serverDetailsUtilsConcurrentHashMap.get(e.getId()) != null
                && serverDetailsUtilsConcurrentHashMap.get(e.getId()).getSshUtils().isAlive()){
            serverDetailsUtils = serverDetailsUtilsConcurrentHashMap.get(e.getId());
            connect = true;
        }
        else{
            connect = serverDetailsUtils.connect(e.getIp(), e.getPort(), e.getLoginUsername(), e.getLoginPassword());
            // 如果链接成功 则存入缓存
            if (connect) {
                serverDetailsUtilsConcurrentHashMap.put(e.getId(), serverDetailsUtils);
            }
        }

        // 判断当前服务器的操作系统
        // TODO 此处需要根据操作系统创建对应的指令集对象 当前默认采用ubuntu
        if (connect) {
            ServerHardwareInfo serverHardwareInfo = serverDetailsUtils.getServerHardwareInfo();
            // 将信息更新至数据库
            baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                    .eq(ServerInfo::getId, e.getId())
                    .set(ServerInfo::getCpu, serverHardwareInfo.getCpu())
                    .set(ServerInfo::getOperatingSystem, serverHardwareInfo.getOs())
                    .set(ServerInfo::getDiskSpace, serverHardwareInfo.getTotalDiskSpace())
                    .set(ServerInfo::getFreeDiskSpace, serverHardwareInfo.getFreeDiskSpace())
                    .set(ServerInfo::getMemorySpace, serverHardwareInfo.getTotalMemSpace())
                    .set(ServerInfo::getFreeMemorySpace, serverHardwareInfo.getFreeMemSpace())
                    .set(ServerInfo::getLastUpdate, new Date())
                    .set(ServerInfo::getStatus, "在线")
            );
        }
        else {
            baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                    .eq(ServerInfo::getId, e.getId())
                    .set(ServerInfo::getStatus, "离线")
                    .set(ServerInfo::getLastUpdate, new Date()));
        }
    }
}

