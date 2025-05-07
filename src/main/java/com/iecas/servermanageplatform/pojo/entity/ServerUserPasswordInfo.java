package com.iecas.servermanageplatform.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (ServerUserPasswordInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerUserPasswordInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 用户id
     */
    private Long userId;
    
}

