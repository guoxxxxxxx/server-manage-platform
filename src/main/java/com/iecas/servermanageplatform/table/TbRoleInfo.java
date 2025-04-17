/**
 * @Time: 2025/4/17 15:46
 * @Author: guoxun
 * @File: TbRoleInfo
 * @Description:
 */

package com.iecas.servermanageplatform.table;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tb_role_info")
public class TbRoleInfo {

    @Id
    @Comment("主键")
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "INT8 AUTO_INCREMENT")
    private Long id;

    @Comment("角色名")
    @Column(name = "name")
    private String name;

    @Comment("是否删除")
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;
}
