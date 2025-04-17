package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (ServerInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Data
public class ServerInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    
    /**
     * 配置详情
     */
    private String configurationDetails;
    
    /**
     * CPU信息
     */
    private String cpu;
    
    /**
     * 是否删除
     */
    private Integer deleted;
    
    /**
     * 硬盘空间
     */
    private Integer diskSpace;
    
    /**
     * 服务器IP
     */
    private String ip;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 最后上线时间
     */
    private Date lastOnlineTime;
    
    /**
     * 放置地点
     */
    private String location;
    
    /**
     * 登录密码
     */
    private String loginPassword;
    
    /**
     * 登录用户名
     */
    private String loginUsername;
    
    /**
     * 内存空间
     */
    private Integer memorySpace;
    
    /**
     * 服务器名称
     */
    private String name;
    
    /**
     * 备注
     */
    private String note;
    
    /**
     * 操作系统
     */
    private String operatingSystem;
    
    /**
     * 所有人
     */
    private String owner;
    
    /**
     * 价格
     */
    private Object price;
    
    /**
     * 负责人
     */
    private String principal;
    
    /**
     * 维修人员
     */
    private String repairer;
    
    /**
     * 维修人员电话
     */
    private String repairerPhone;
    
}

