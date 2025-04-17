package com.iecas.servermanageplatform.pojo.entity;


import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class RoleInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    
    /**
     * 角色名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否删除
     */
    private Integer deleted;

}

