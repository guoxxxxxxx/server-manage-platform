package com.iecas.servermanageplatform.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.iecas.servermanageplatform.entity.ServerInfo;

/**
 * (ServerInfo)表数据库访问层
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Mapper
public interface ServerInfoDao extends BaseMapper<ServerInfo> {
}

