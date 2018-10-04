package com.tcg.admin.to;

import java.io.Serializable;

public class QueryOperatorsTO implements Serializable {

    private static final long serialVersionUID = 8313268060626726478L;

    private Integer operatorId;

    /** 管理員名稱 */
    private String operatorName;

    /** 暱稱 */
    private String nickname;

    /**
     * 啟用狀態 0:手動禁用 | 1:可登入 | 2:登入禁用 | 7:帳號已刪
     */
    private Integer activeFlag;

    private String categoryName;

    private String merchantNames;

    private String roleNames;

    private String merchantNamesForTb;

    private String roleNamesForTb;

    private Merchants merchants;

    private Roles roles;

    private String baseMerchantCode;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Merchants getMerchants() {
        return merchants;
    }

    public void setMerchants(Merchants merchants) {
        this.merchants = merchants;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMerchantNames() {
        return merchantNames;
    }

    public void setMerchantNames(String merchantNames) {
        this.merchantNames = merchantNames;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getMerchantNamesForTb() {
        return merchantNamesForTb;
    }

    public void setMerchantNamesForTb(String merchantNamesForTb) {
        this.merchantNamesForTb = merchantNamesForTb;
    }

    public String getRoleNamesForTb() {
        return roleNamesForTb;
    }

    public void setRoleNamesForTb(String roleNamesForTb) {
        this.roleNamesForTb = roleNamesForTb;
    }

    public String getBaseMerchantCode() {
        return baseMerchantCode;
    }

    public void setBaseMerchantCode(String baseMerchantCode) {
        this.baseMerchantCode = baseMerchantCode;
    }
}
