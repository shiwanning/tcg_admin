package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "US_OPERATOR_AUTH")
public class OperatorAuth extends BaseAuditEntity {

    @Id
    @SequenceGenerator(name = "SEQ_US_OPERATOR_AUTH", sequenceName = "SEQ_US_OPERATOR_AUTH", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_US_OPERATOR_AUTH")
    @Column(name = "RID")
    private Long rid;
    
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;
    
    @Column(name = "AUTH_TYPE")
    private String authType;
    
    @Column(name = "AUTH_KEY")
    private String authKey;
    
    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    
    @Column(name = "LAST_PASS_TIME")
    private Date lastPassTime;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public Date getLastPassTime() {
        return lastPassTime;
    }

    public void setLastPassTime(Date lastPassTime) {
        this.lastPassTime = lastPassTime;
    }
    
    
    public static enum Status {
        INACTIVE,
        ACTIVE;
    }
    
    
}
