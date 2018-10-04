package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Title: com.tcg.admin.model.UserPrincipal.java
 * <p>
 * Description:
 * @version: 1.0
 */
@MappedSuperclass
public class UserPrincipal extends BaseEntity {

    /** 密碼Hash */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * 啟用狀態 0:手動禁用 | 1:可登入 | 2:登入禁用 | 7:帳號已刪
     */
    @Column(name = "ACTIVE_FLAG")
    private Integer activeFlag;

    /**
     * 登入錯誤次數
     */
    @Column(name = "ERROR_COUNT")
    private Integer errorCount = 0;
    
    /**
     * 登入禁用時間
     */
    @Column(name = "ERROR_TIME")
    private Date errorTime;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Date getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(Date errorTime) {
        this.errorTime = errorTime;
    }
    
    
    
    
}
