/**
 * @Time: 2025/4/17 16:03
 * @Author: guoxun
 * @File: TbServerUserAuthorityInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tb_server_user_authority_info")
public class TbServerUserAuthorityInfo {

    @Id
    @Comment("主键")
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("用户id")
    @Column(name = "user_id")
    private Long userId;

    @Comment("服务器id")
    @Column(name = "server_id")
    private Long serverId;

    @Comment("是否可以访问")
    @Column(name = "can_access", columnDefinition = "TINYINT(1)")
    private Boolean canAccess;

    @Comment("是否删除")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;
}
