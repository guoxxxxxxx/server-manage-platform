/**
 * @Time: 2025/4/22 9:06
 * @Author: guoxun
 * @File: UserLoginDTO
 * @Description:
 */

package com.iecas.servermanageplatform.pojo.dto;


import lombok.Data;


@Data
public class UserLoginDTO {

    /**
     * 用户名或邮箱
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
