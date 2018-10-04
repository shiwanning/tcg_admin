package com.tcg.admin.to;

import java.io.Serializable;

public class QueryCategoriesTO implements Serializable {

    private static final long serialVersionUID = 1926974007403333403L;

    private Integer categoryId;

    /** 管理員名稱 */
    private String categoryName;

    private Integer roleId;

    private String roleName;

    /**
     * 啟用狀態 0:手動禁用 | 1:可登入 | 2:登入禁用 | 7:帳號已刪
     */
    private Integer activeFlag;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }
}
