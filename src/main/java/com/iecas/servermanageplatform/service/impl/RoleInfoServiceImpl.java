package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.dao.RoleInfoDao;
import com.iecas.servermanageplatform.pojo.entity.RoleInfo;
import com.iecas.servermanageplatform.service.RoleInfoService;
import org.springframework.stereotype.Service;

/**
 * (RoleInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:25
 */



@Service("roleInfoService")
public class RoleInfoServiceImpl extends ServiceImpl<RoleInfoDao, RoleInfo> implements RoleInfoService {

}

