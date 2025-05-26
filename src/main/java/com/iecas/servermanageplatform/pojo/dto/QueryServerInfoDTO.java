package com.iecas.servermanageplatform.pojo.dto;


import lombok.Data;



@Data
public class QueryServerInfoDTO {

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 当前页
     */
    private int pageNo;

    /**
     * 查询参数
     */
    private String queryParams;

    /**
     * 是否只显示在线服务器
     */
    private boolean onlyShowOnline;
}
