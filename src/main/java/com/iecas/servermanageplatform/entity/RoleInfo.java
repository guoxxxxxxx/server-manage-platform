package com.iecas.servermanageplatform.entity;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * (RoleInfo)表实体类
 *
 * @author guox
 * @since 2025-04-17 16:21:20
 */



@Data
public class RoleInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    
    /**
     * 是否删除
     */
    private Integer deleted;
    
    /**
     * 角色名
     */
    private String name;
    
}

