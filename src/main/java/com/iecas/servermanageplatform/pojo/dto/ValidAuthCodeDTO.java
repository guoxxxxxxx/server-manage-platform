/**
 * @Time: 2025/4/22 16:31
 * @Author: guoxun
 * @File: ValidAuthCodeDTO
 * @Description:
 */

package com.iecas.servermanageplatform.pojo.dto;


import lombok.Data;



@Data
public class ValidAuthCodeDTO {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String authCode;
}
