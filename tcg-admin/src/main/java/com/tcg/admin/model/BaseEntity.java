package com.tcg.admin.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
public class BaseEntity {

    @Version
    @Column(name = "VERSION", columnDefinition = "Number(12,0)")
    private Long version;
    /**
     * 新增時間
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", updatable = false)
    private Date createTime;
    /**
     * 更新時間
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    public Long getVersion() {
        return version;
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

    @PrePersist
    public void onCreate() {
        Date now = new Date();
        createTime = createTime == null ? now : createTime;
        updateTime = updateTime == null ? now : updateTime;
    }

    @PreUpdate
    public void onUpdate() {
        updateTime = new Date();
    }
}