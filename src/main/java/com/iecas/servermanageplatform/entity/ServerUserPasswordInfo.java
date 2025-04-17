package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (ServerUserPasswordInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Data
public class ServerUserPasswordInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    
    /**
     * 是否删除
     */
    private Integer deleted;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 服务器id
     */
    private Long serverId;
    
    /**
     * 用户名
     */
    private String username;
    
}

