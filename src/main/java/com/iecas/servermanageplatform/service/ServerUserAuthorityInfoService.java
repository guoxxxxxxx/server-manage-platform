package com.iecas.servermanageplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecas.servermanageplatform.pojo.entity.ServerUserAuthorityInfo;
import com.iecas.servermanageplatform.pojo.vo.UserAuthServerVO;

import java.util.List;

/**
 * (ServerUserAuthorityInfo)表服务接口
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



public interface ServerUserAuthorityInfoService extends IService<ServerUserAuthorityInfo> {


    /**
     * 获取当前服务器的用户权限信息
     * @param id 服务器id
     * @return 用户权限信息
     */
    List<UserAuthServerVO> getCurrentServerUserAuth(Long id);


    /**
     * 更新用户权限信息
     * @param serverId 服务器id
     * @param userId 用户id
     * @param auth 权限
     * @return 是否更新成功
     */
    boolean updateUserAuthInfo(Long serverId, Long userId, Boolean auth);
}

