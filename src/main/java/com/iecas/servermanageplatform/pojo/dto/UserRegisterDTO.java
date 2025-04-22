/**
 * @Time: 2025/4/18 14:36
 * @Author: guoxun
 * @File: UserRegisterDTO
 * @Description: 用户注册信息DTO
 */

package com.iecas.servermanageplatform.pojo.dto;

import lombok.Data;



@Data
public class UserRegisterDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String authCode;

}
