/**
 * @Time: 2025/4/17 16:00
 * @Author: guoxun
 * @File: TbServerRoleAuthorityInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tb_server_role_authority_info")
public class TbServerRoleAuthorityInfo {

    @Id
    @Comment("主键id")
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("角色id")
    @Column(name = "role_id")
    private Long roleId;

    @Comment("服务器id")
    @Column(name = "server_id")
    private Long serverId;

    @Comment("是否可访问")
    @Column(name = "can_access", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean canAccess;

    @Comment("是否删除")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;
}
