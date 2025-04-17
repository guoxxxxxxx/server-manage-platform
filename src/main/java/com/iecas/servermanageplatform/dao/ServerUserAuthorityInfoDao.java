package com.iecas.servermanageplatform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.iecas.servermanageplatform.pojo.entity.ServerUserAuthorityInfo;

/**
 * (ServerUserAuthorityInfo)表数据库访问层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Mapper
public interface ServerUserAuthorityInfoDao extends BaseMapper<ServerUserAuthorityInfo> {
}

