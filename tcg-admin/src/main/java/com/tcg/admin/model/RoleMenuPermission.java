package com.tcg.admin.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * <p>Title: com.tcg.admin.model.RoleMenuPermission</p>
 * <p>Description: 角色能見到那些選單的對應資料表</p>
 *
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_ROLE_MENU_PERMISSION")
public class RoleMenuPermission extends BaseEntity {


    /**
     * <code>serialVersionUID</code> 的註解
     */
    private static final long serialVersionUID = 7614254572164815416L;

    /** 主鍵 */
    @Id
    @SequenceGenerator(name = "SEQ_ROLE_MENU_PERMISSION", sequenceName = "SEQ_ROLE_MENU_PERMISSION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE_MENU_PERMISSION")
    @Column(name = "SEQ_ID")
    private Integer seqId;

    /** 外部鍵(角色) */
    @Column(name = "ROLE_ID")
    private Integer roleId;


    /** 外部鍵(管理員) */
    @Column(name = "MENU_ID")
    private Integer menuId;


    public Integer getSeqId() {
        return seqId;
    }


    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }


    public Integer getRoleId() {
        return roleId;
    }


    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }


    public Integer getMenuId() {
        return menuId;
    }


    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
}
