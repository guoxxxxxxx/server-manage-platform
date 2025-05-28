package com.iecas.servermanageplatform.pojo.dto;

import lombok.Data;

/**
 * @Author: guo_x
 * @Date: 2025/5/28 9:42
 * @Description: 修改用户角色
 */
@Data
public class ChangeUserRoleDTO {

    /**
     * 目标用户id
     */
    private Long targetUserId;

    /**
     * 目标角色所要更改的角色id
     */
    private Long roleId;
}
