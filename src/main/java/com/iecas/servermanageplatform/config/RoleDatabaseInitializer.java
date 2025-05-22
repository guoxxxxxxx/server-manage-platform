/**
 * @Time: 2025/2/7 18:54
 * @Author: guoxun
 * @File: AuthDatabaseInitializer
 * @Description: 启动时自动检测数据库并初始化角色和权限
 * TODO 此处采用硬编码显然是不合理的, 后续需要修改为从配置文件中读取配置信息
 */

package com.iecas.servermanageplatform.config;

import com.iecas.servermanageplatform.pojo.entity.RoleInfo;
import com.iecas.servermanageplatform.service.RoleInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class RoleDatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleInfoService roleInfoService;

    @Override
    public void run(String... args) {
        log.info("🔍 正在检测数据库角色和权限数据...");

        if (roleInfoService.count() == 0) {
            log.info("🚀 角色表为空，开始初始化角色数据...");
            insertRoles();
        }

        log.info("✅ 数据库初始化完成！");
    }

    private void insertRoles() {
        List<RoleInfo> roles = Arrays.asList(
                new RoleInfo(1L,  "SUPER_SYSTEM_ADMIN", "超级系统管理员", 0),
                new RoleInfo(2L,  "SUPER_ADMIN", "超级管理员", 0),
                new RoleInfo(3L,  "ADMIN", "管理员", 0),
                new RoleInfo(4L,  "USER", "普通用户", 0),
                new RoleInfo(5L,  "NON_AUTHORITY_USER", "无权限用户", 0)
        );
        roleInfoService.saveBatch(roles);
    }
}
