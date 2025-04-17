package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (ServerUserAuthorityInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Data
public class ServerUserAuthorityInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    
    /**
     * 是否可以访问
     */
    private Object canAccess;
    
    /**
     * 是否删除
     */
    private Integer deleted;
    
    /**
     * 服务器id
     */
    private Long serverId;
    
    /**
     * 用户id
     */
    private Long userId;
    
}

