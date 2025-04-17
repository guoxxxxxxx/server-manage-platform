package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.dao.ServerInfoDao;
import com.iecas.servermanageplatform.entity.ServerInfo;
import com.iecas.servermanageplatform.service.ServerInfoService;
import org.springframework.stereotype.Service;

/**
 * (ServerInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Service("serverInfoService")
public class ServerInfoServiceImpl extends ServiceImpl<ServerInfoDao, ServerInfo> implements ServerInfoService {

}

