package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (ServerRoleAuthorityInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Data
public class ServerRoleAuthorityInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    
    /**
     * 是否可访问
     */
    private Integer canAccess;
    
    /**
     * 是否删除
     */
    private Integer deleted;
    
    /**
     * 角色id
     */
    private Long roleId;
    
    /**
     * 服务器id
     */
    private Long serverId;
    
}

