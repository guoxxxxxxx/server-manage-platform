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

    /**
     * 是否仅显示白名单信息，true 仅显示白名单， false 仅显示非白名单， null都显示
     */
    private Boolean inWhite;
}
