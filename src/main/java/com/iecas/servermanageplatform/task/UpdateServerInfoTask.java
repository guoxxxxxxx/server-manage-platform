package com.iecas.servermanageplatform.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.iecas.servermanageplatform.pojo.entity.ServerHardwareInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.enums.OSEnum;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsFactory;
import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * @Author: guo_x
 * @Date: 2025/5/9 9:46
 * @Description: 更新服务器信息线程
 */
@Component
public class UpdateServerInfoTask {

    @Resource
    ServerInfoService serverInfoService;



}
