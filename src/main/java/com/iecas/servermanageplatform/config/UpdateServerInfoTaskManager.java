package com.iecas.servermanageplatform.config;

import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.task.UpdateServerInfoTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: guo_x
 * @Date: 2025/5/9 11:03
 * @Description:
 */
@Slf4j
@Component
public class UpdateServerInfoTaskManager {

    @Resource
    ServerInfoService serverInfoService;


    @Scheduled(fixedDelay = 60000)
    public void update(){
        serverInfoService.updateServerHardwareInfo(null);
        log.debug("服务器硬件状态信息已更新!");
    }
}
