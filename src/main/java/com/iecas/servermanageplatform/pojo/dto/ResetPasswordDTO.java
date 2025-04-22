/**
 * @Time: 2025/4/22 17:26
 * @Author: guoxun
 * @File: ResetPasswordDTO
 * @Description:
 */

package com.iecas.servermanageplatform.pojo.dto;


import lombok.Data;



@Data
public class ResetPasswordDTO {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String authCode;
}
