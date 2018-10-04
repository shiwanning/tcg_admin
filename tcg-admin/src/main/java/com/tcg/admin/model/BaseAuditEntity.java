package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class BaseAuditEntity {

    @Column(name = "CREATE_TIME", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    @Column(name = "UPDATE_TIME", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    
    @Column(name = "CREATE_OPERATOR_NAME")
    private String createOperatorName;
    
    @Column(name = "UPDATE_OPERATOR_NAME")
    private String updateOperatorName;
    
    @PreUpdate
    protected void onUpdate() {
       updateTime = new Date();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }

    public String getUpdateOperatorName() {
        return updateOperatorName;
    }

    public void setUpdateOperatorName(String updateOperatorName) {
        this.updateOperatorName = updateOperatorName;
    }
    
}
