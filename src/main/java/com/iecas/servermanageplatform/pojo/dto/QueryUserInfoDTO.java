package com.iecas.servermanageplatform.pojo.dto;

import lombok.Data;

/**
 * @Author: guo_x
 * @Date: 2025/5/27 15:53
 * @Description: 分页查询用户信息参数
 */
@Data
public class QueryUserInfoDTO {

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 当前页码
     */
    private Integer pageNo;

    /**
     * 查询参数
     */
    private String queryParams;
}
