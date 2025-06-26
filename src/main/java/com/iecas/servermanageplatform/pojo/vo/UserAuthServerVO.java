package com.iecas.servermanageplatform.pojo.vo;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: guo_x
 * @Date: 2025/6/25 16:55
 * @Description: 用户权限管理VO界面
 */

@Data
public class UserAuthServerVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色名
     */
    private String roleName;

    /**
     * 用户对当前服务器的访问权限
     */
    private boolean canAccess;

    /**
     * 当前服务器id
     */
    private Long serverId;
}
