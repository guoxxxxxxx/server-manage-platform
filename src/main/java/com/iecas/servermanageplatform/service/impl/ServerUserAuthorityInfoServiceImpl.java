package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.config.UserThreadLocal;
import com.iecas.servermanageplatform.dao.ServerUserAuthorityInfoDao;
import com.iecas.servermanageplatform.exception.CommonException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import com.iecas.servermanageplatform.pojo.entity.ServerUserAuthorityInfo;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.pojo.vo.UserAuthServerVO;
import com.iecas.servermanageplatform.service.ServerInfoService;
import com.iecas.servermanageplatform.service.ServerUserAuthorityInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * (ServerUserAuthorityInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */
@Service("serverUserAuthorityInfoService")
public class ServerUserAuthorityInfoServiceImpl extends ServiceImpl<ServerUserAuthorityInfoDao, ServerUserAuthorityInfo> implements ServerUserAuthorityInfoService {

    @Resource
    ServerInfoService serverInfoService;


    @Override
    public List<UserAuthServerVO> getCurrentServerUserAuth(Long id) {
        return baseMapper.getCurrentServerUserAuth(id);
    }


    @Override
    public boolean updateUserAuthInfo(Long serverId, Long userId, Boolean auth) {
        // 鉴权
        UserInfo currentUser = UserThreadLocal.getUserInfo();
        ServerInfo currentServer = serverInfoService.getById(serverId);
        if (currentUser.getRoleId() <= 3 || currentServer.getUserId().equals(currentServer.getId())){
            // 检查权限记录表中是否存在当前用户对应的当前服务器的权限信息
            List<ServerUserAuthorityInfo> authList = baseMapper.selectList(new LambdaQueryWrapper<ServerUserAuthorityInfo>()
                    .eq(ServerUserAuthorityInfo::getServerId, serverId)
                    .eq(ServerUserAuthorityInfo::getUserId, userId));
            // 如果不存在则创建新的记录并添加到数据库中
            if (authList.isEmpty()){
                ServerUserAuthorityInfo record = ServerUserAuthorityInfo.builder()
                        .serverId(serverId)
                        .userId(userId)
                        .canAccess(auth).build();
                baseMapper.insert(record);
                return auth;
            }
            else if (authList.size() == 1){
                ServerUserAuthorityInfo current = authList.get(0);
                current.setCanAccess(auth);
                return auth;
            }
            else {
                List<Long> willDeleted = new ArrayList<>();
                for (ServerUserAuthorityInfo e: authList){
                    willDeleted.add(e.getId());
                }
                baseMapper.deleteBatchIds(willDeleted);
                throw new CommonException("检测到数据库出现不一致问题, 已进行恢复!");
            }
        }
        else {
            throw new WarningTipsException("当前用户无权限");
        }
    }
}

