package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.dao.ServerUserPasswordInfoDao;
import com.iecas.servermanageplatform.entity.ServerUserPasswordInfo;
import com.iecas.servermanageplatform.service.ServerUserPasswordInfoService;
import org.springframework.stereotype.Service;

/**
 * (ServerUserPasswordInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Service("serverUserPasswordInfoService")
public class ServerUserPasswordInfoServiceImpl extends ServiceImpl<ServerUserPasswordInfoDao, ServerUserPasswordInfo> implements ServerUserPasswordInfoService {

}

