/**
 * @Time: 2025/4/17 15:48
 * @Author: guoxun
 * @File: TbServerInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "tb_server_info")
public class TbServerInfo {

    @Id
    @Comment("主键")
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("服务器名称")
    @Column(name = "name")
    private String name;

    @Comment("服务器IP")
    @Column(name = "ip")
    private String ip;

    @Comment("操作系统")
    @Column(name = "operating_system")
    private String operatingSystem;

    @Comment("所有人")
    @Column(name = "owner")
    private String owner;

    @Comment("放置地点")
    @Column(name = "location")
    private String location;

    @Comment("维修人员")
    @Column(name = "repairer")
    private String repairer;

    @Comment("维修人员电话")
    @Column(name = "repairer_phone")
    private String repairerPhone;

    @Comment("价格")
    @Column(name = "price")
    private Double price;

    @Comment("负责人")
    @Column(name = "principal")
    private String principal;

    @Comment("配置详情")
    @Column(name = "configuration_details", columnDefinition = "LONGTEXT")
    private String configurationDetails;

    @Comment("备注")
    @Column(name = "note", columnDefinition = "LONGTEXT")
    private String note;

    @Comment("CPU信息")
    @Column(name = "cpu")
    private String cpu;

    @Comment("内存空间")
    @Column(name = "memory_space")
    private Integer memorySpace;

    @Comment("可用内存空间")
    @Column(name = "freeMemorySpace")
    private Integer freeMemorySpace;

    @Comment("硬盘空间")
    @Column(name = "disk_space")
    private Integer diskSpace;

    @Comment("可用硬盘空间")
    @Column(name = "free_disk_space")
    private Integer freeDiskSpace;

    @Comment("最后上线时间")
    @Column(name = "last_online_time")
    private Date lastOnlineTime;

    @Comment("最后登录时间")
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Comment("登录用户名")
    @Column(name = "login_username")
    private String loginUsername;

    @Comment("登录密码")
    @Column(name = "login_password")
    private String loginPassword;

    @Comment("是否删除")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;

    @Comment("服务器状态")
    @Column(name = "status")
    private String status;

    @Comment("上一次更新时间")
    @Column(name = "last_update")
    private Date lastUpdate;

    @Comment("添加人")
    @Column(name = "user_id")
    private Long userId;

    @Comment("端口号")
    @Column(name = "port", columnDefinition = "varchar(16) default '22'")
    private String port;
}
