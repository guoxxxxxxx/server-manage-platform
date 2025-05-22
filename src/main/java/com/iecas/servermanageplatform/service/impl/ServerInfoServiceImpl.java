package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.config.UserThreadLocal;
import com.iecas.servermanageplatform.dao.ServerInfoDao;
import com.iecas.servermanageplatform.exception.CommonException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.dto.QueryServerInfoDTO;
import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerUserPasswordInfo;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.pojo.enums.OSEnum;
import com.iecas.servermanageplatform.pojo.enums.ServerStatusEnum;
import com.iecas.servermanageplatform.pojo.vo.AddServerInfoVO;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.service.ServerUserPasswordInfoService;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsFactory;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsUtils;
import com.iecas.servermanageplatform.utils.serverDetails.ServerOnlineChecker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * (ServerInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */


@Slf4j
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
                    .eq(ServerInfo::getIp, serverInfo.getIp())
                    .eq(ServerInfo::getPort, serverInfo.getPort()));
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

        // 密码是否正确
        boolean pwdIsCorrect = true;

        // 连接ssh
        try {
            serverDetailsUtils.connect(serverInfo.getIp(), Integer.parseInt(serverInfo.getPort()),
                    serverInfo.getLoginUsername(), serverInfo.getLoginPassword());
        } catch (UserAuthException userAuthException){
            pwdIsCorrect = false;
            log.debug("当前服务器密码错误!");
        }

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
        serverInfo.setPwdIsCorrect(pwdIsCorrect);
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
                log.debug("目标主机链接失败!");
            }
        }
        executorService.shutdown();
    }


    @Override
    public boolean shutdownById(Long serverId) {
        // 查询当前服务器信息
        ServerInfo serverInfo = baseMapper.selectById(serverId);
        // 判断当前服务器是否在线
        if (serverInfo.getStatus().equals("离线")){
            log.info("服务器id: {} 当前服务器已经离线!", serverInfo);
            throw new WarningTipsException("当前服务器已离线");
        }
        // 获取当前服务器的指令集对象
        ServerDetailsUtils serverDetailsUtilsByServerId = getServerDetailsUtilsByServerId(serverId);
        // 如果对象为null则返回失败消息
        if (serverDetailsUtilsByServerId == null){
            log.debug("服务器指令集对象连接失败!");
            throw new WarningTipsException("连接服务器失败");
        }
        // 更新服务器信息为准备关机
        serverInfo.setStatus(ServerStatusEnum.SHUTDOWN.getStatus());
        baseMapper.updateById(serverInfo);
        return serverDetailsUtilsByServerId.shutdown(serverInfo.getLoginPassword());
    }


    @Override
    public boolean cancelShutdownById(Long serverId) {
        ServerDetailsUtils serverDetailsUtilsByServerId = getServerDetailsUtilsByServerId(serverId);
        if (serverDetailsUtilsByServerId == null){
            log.debug("服务器指令集对象连接失败!");
            throw new WarningTipsException("连接服务器失败");
        }
        // 执行取消关机指令
        boolean b = serverDetailsUtilsByServerId.cancelShutDown();
        // 更新服务器状态
        baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                .eq(ServerInfo::getId, serverId)
                .set(b, ServerInfo::getStatus, ServerStatusEnum.ONLINE.getStatus()));
        return b;
    }


    @Override
    public boolean rebootById(Long serverId) {
        // 判断服务器状态
        ServerInfo serverInfo = baseMapper.selectById(serverId);
        if (!serverInfo.getStatus().toLowerCase().equals("在线")){
            log.debug("服务器未在线, 重启失败!");
            return false;
        }
        // 获取当前服务器指令集对象
        ServerDetailsUtils serverDetailsUtils = getServerDetailsUtilsByServerId(serverId);
        // 重启服务器
        boolean reboot = serverDetailsUtils.reboot(serverInfo.getLoginPassword());
        // 更新服务器信息
        if (reboot) {
            baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                    .eq(ServerInfo::getId, serverId)
                    .set(ServerInfo::getStatus, "重启中"));
        }
        else {
            log.info("重启服务器失败, 可能的原因: 默认用户名密码错误!");
        }
        return reboot;
    }


    @Override
    public Map<String, Object> shutdownByIds(List<Long> serverIdList) {
        // 将要关闭的服务器的信息列表
        List<ServerInfo> allServerInfoList;
        if (serverIdList == null || serverIdList.isEmpty()){
            allServerInfoList = baseMapper.selectList(null);
        }
        else {
            allServerInfoList = baseMapper.selectList(new LambdaQueryWrapper<ServerInfo>()
                    .in(ServerInfo::getId, serverIdList));
        }
        // 用于存储结果
        Map<String, Object> result = new HashMap<>();
        List<String> failList = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        for (ServerInfo e: allServerInfoList){
            // 检查当前服务器是否在线或密码是否正确 若不在线或密码错误则直接跳过该服务器
            if (!e.getStatus().equals("在线")){
                failList.add("服务器: " + e.getIp() + ":" + e.getPort() + " 关闭失败, 原因: 当前服务器未在线！" );
                continue;
            }
            else if(!e.getPwdIsCorrect()){
                failList.add("服务器: " + e.getIp() + ":" + e.getPort() + " 关闭失败, 原因: 当前服务器用户名密码错误！" );
                continue;
            }
            // 获取当前服务器的会话对象
            ServerDetailsUtils currentServerSession = getServerDetailsUtilsByServerId(e.getId());
            boolean shutdown = currentServerSession.shutdown(e.getLoginPassword());
            if (shutdown){
                successList.add("服务器: " + e.getIp() + ":" + e.getPort() + " 关闭成功, 服务器将在60s内关闭!");
                // 修改服务器状态
                baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                        .eq(ServerInfo::getId, e.getId())
                        .set(ServerInfo::getStatus, ServerStatusEnum.SHUTDOWN.getStatus()));
            }
            else {
                failList.add("服务器: " + e.getIp() + ":" + e.getPort() + " 关闭失败, 原因: 未知错误！" );
            }
        }
        result.put("success", successList);
        result.put("fail", failList);
        return result;
    }


    @Override
    public Map<String, Object> cancelShutdown() {
        // 获取所有服务器信息
        List<ServerInfo> serverInfoList = baseMapper.selectList(null);
        List<ServerInfo> successList = new ArrayList<>();
        List<ServerInfo> failList = new ArrayList<>();
        // 检查服务器状态是否为待关闭状态
        for (ServerInfo e : serverInfoList){
            try {
                // 如果是则获取到相应的对象，并取消关闭服务器
                if (e.getStatus().equals(ServerStatusEnum.SHUTDOWN.getStatus())) {
                    // 获取服务器控制端对象 并 取消关闭服务器
                    ServerDetailsUtils serverDetailsUtils = getServerDetailsUtilsByServerId(e.getId());
                    serverDetailsUtils.cancelShutDown();
                    // 更新数据库中的状态信息
                    baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                            .eq(ServerInfo::getId, e.getId())
                            .set(ServerInfo::getStatus, ServerStatusEnum.ONLINE));
                    successList.add(e);
                }
            } catch (Exception exception){
                log.error("取消关闭服务器异常", exception);
                failList.add(e);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("fail", failList);
        result.put("success", successList);
        return result;
    }


    /**
     * 更新单个服务器信息
     * @param e 更新的服务器信息
     */
    private void updateSingleServerInfo(ServerInfo e){
        // 当前操作系统的指令集对象
        ServerDetailsUtils serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);

        // 是否链接成功
        boolean connect = false;
        // 密码是否正确
        boolean pwdIsCorrect = true;

        // 判断缓存中是否有当前对象的链接
        if (serverDetailsUtilsConcurrentHashMap.get(e.getId()) != null
                && serverDetailsUtilsConcurrentHashMap.get(e.getId()).getSshUtils().isAlive()){
            serverDetailsUtils = serverDetailsUtilsConcurrentHashMap.get(e.getId());
            connect = true;
        }
        else{
            try {
                connect = serverDetailsUtils.connect(e.getIp(), e.getPort(), e.getLoginUsername(), e.getLoginPassword());
            } catch (UserAuthException userAuthException){
                // 密码错误, 修改数据库标识
                pwdIsCorrect = false;
            }
            // 如果链接成功 则存入缓存
            if (connect) {
                serverDetailsUtilsConcurrentHashMap.put(e.getId(), serverDetailsUtils);
            }
            // 如果失败则删除缓存
            else {
                serverDetailsUtilsConcurrentHashMap.remove(e.getId());
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
                    .set(e.getStatus() == null || !e.getStatus().equalsIgnoreCase(ServerStatusEnum.SHUTDOWN.getStatus()) || e.getStatus().isEmpty(), ServerInfo::getStatus, "在线")
                    .set(ServerInfo::getPwdIsCorrect, pwdIsCorrect)
            );
        }
        else {
            // 检查当前服务器是否在线
            boolean isOnline = ServerOnlineChecker.isPortOpen(e.getIp(), e.getPort(), 500);
            baseMapper.update(new LambdaUpdateWrapper<ServerInfo>()
                    .eq(ServerInfo::getId, e.getId())
                    .set(!e.getStatus().equalsIgnoreCase(ServerStatusEnum.SHUTDOWN.getStatus()) || !isOnline || e.getStatus().isEmpty(), ServerInfo::getStatus, isOnline ? "在线" : "离线")
                    .set(ServerInfo::getPwdIsCorrect, pwdIsCorrect)
                    .set(ServerInfo::getLastUpdate, new Date()));
        }
    }


    /**
     * 根据服务器id获取当前服务器的连接对象
     * @param serverId 服务器id
     * @return 指令集对象
     */
    private ServerDetailsUtils getServerDetailsUtilsByServerId(Long serverId){

        // 查询缓存中是否有当前服务器链接对象
        if (serverDetailsUtilsConcurrentHashMap.get(serverId) != null &&
            serverDetailsUtilsConcurrentHashMap.get(serverId).getSshUtils().isAlive()){
            return serverDetailsUtilsConcurrentHashMap.get(serverId);
        }
        else {
            ServerDetailsUtils serverDetailsUtils;
            // 根据id查询当前服务器信息
            ServerInfo serverInfo = baseMapper.selectById(serverId);
            // 创建当前服务器的指令集对象
            if (serverInfo.getOperatingSystem().toLowerCase().contains("ubuntu")){
                serverDetailsUtils = ServerDetailsFactory.create(OSEnum.UBUNTU);
            }
            else {
                return null;
            }
            try {
                // 连接服务器
                boolean connect = serverDetailsUtils.connect(serverInfo.getIp(), serverInfo.getPort(),
                        serverInfo.getLoginUsername(), serverInfo.getLoginPassword());

                if (connect){
                    // 如果连接成功则存入缓存中
                    serverDetailsUtilsConcurrentHashMap.put(serverId, serverDetailsUtils);
                    return serverDetailsUtils;
                }
                else {
                    return null;
                }
            } catch (UserAuthException userAuthException){
                log.error("当前服务器用户名密码错误");
                return null;
            }
        }
    }
}

