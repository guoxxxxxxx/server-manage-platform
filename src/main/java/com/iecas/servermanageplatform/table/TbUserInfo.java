/**
 * @Time: 2025/4/17 15:33
 * @Author: guoxun
 * @File: UserInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

import java.util.Date;


@Table(name = "tb_user_info")
@Entity
public class TbUserInfo {

    @Id
    @Comment("用户主键")
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("用户名")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Comment("用户手机号")
    @Column(name = "phone")
    private String phone;

    @Comment("用户邮箱")
    @Column(name = "email")
    private String email;

    @Comment("密码")
    @Column(name = "password")
    private String password;

    @Comment("注册时间")
    @Column(name = "register_time")
    private Date registerTime;

    @Comment("上次登录时间")
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Comment("上次登录ip")
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Comment("用户头像")
    @Column(name = "avatar")
    private String avatar;

    @Comment("用户角色等级id")
    @Column(name = "role_id", columnDefinition = "INT8 DEFAULT 5")
    private Long roleId;

    @Comment("账户锁")
    @Column(name = "locked", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean locked;

    @Comment("删除位")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;
}
