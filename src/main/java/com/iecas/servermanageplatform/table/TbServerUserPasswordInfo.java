/**
 * @Time: 2025/4/17 15:57
 * @Author: guoxun
 * @File: TbServerUserPasswordInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tb_server_user_password_info")
public class TbServerUserPasswordInfo {

    @Id
    @Comment("主键id")
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("服务器id")
    @Column(name = "server_id")
    private Long serverId;

    @Comment("用户名")
    @Column(name = "username")
    private String username;

    @Comment("密码")
    @Column(name = "password")
    private String password;

    @Comment("是否删除")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;

    @Comment("对应用户的id")
    @Column(name = "user_id")
    private Long userId;
}
