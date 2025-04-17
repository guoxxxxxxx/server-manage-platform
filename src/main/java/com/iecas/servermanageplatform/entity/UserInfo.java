package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (UserInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Data
public class UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户主键
     */
    private Long id;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 删除位
     */
    private Integer deleted;
    
    /**
     * 上次登录ip
     */
    private Date lastLoginIp;
    
    /**
     * 上次登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 账户锁
     */
    private Object locked;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 注册时间
     */
    private Date registerTime;
    
    /**
     * 用户角色等级id
     */
    private Long roleId;
    
    /**
     * 用户名
     */
    private String username;
    
}

