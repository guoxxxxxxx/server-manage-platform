package com.iecas.servermanageplatform.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.Comment;

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
    @TableId(type = IdType.AUTO)
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
    private Long diskSpace;
    
    /**
     * 服务器IP
     */
    private String ip;
    
    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;
    
    /**
     * 最后上线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    private Long memorySpace;
    
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


    /**
     * 服务器状态
     */
    private String status;


    /**
     * 上一次更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastUpdate;


    /**
     * 可用内存空间
     */
    private Long freeMemorySpace;


    /**
     * 可用磁盘空间
     */
    private Long freeDiskSpace;


    /**
     * 添加人
     */
    private Long userId;


    /**
     * 端口号
     */
    private String port;


    /**
     * 默认账户密码是否正确
     */
    private Boolean pwdIsCorrect;


    /**
     * 服务器关机优先级
     */
    private Integer shutdownRank;
}

