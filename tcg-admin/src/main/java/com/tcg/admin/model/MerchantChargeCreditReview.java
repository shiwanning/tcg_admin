package com.tcg.admin.model;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TAC_MERCHANT_CHARGE_CREDIT_REVIEW")
public class MerchantChargeCreditReview extends BaseAuditEntity{
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_CREDIT_REVIEW", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_CREDIT_REVIEW", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_CREDIT_REVIEW")
    @Column(name = "RID", nullable = false)
    private Long rid;
    @Basic
    @Column(name = "MERCHANT_CODE", nullable = false, length = 32)
    private String merchantCode;
    @Basic
    @Column(name = "LEVER_MULTIPLIER", nullable = false)
    private BigDecimal leverMultiplier;
    @Basic
    @Column(name = "MODIFY_LEVER_MULTIPLIER", nullable = false)
    private BigDecimal modifyLeverMultiplier;
    @Basic
    @Column(name = "STATUS", nullable = false)
    private Integer status;
    @Basic
    @Column(name = "REMARK", nullable = false, length = 200)
    private String remark;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }


    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public BigDecimal getLeverMultiplier() {
        return leverMultiplier;
    }

    public void setLeverMultiplier(BigDecimal leverMultiplier) {
        this.leverMultiplier = leverMultiplier;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getModifyLeverMultiplier() {
        return modifyLeverMultiplier;
    }

    public void setModifyLeverMultiplier(BigDecimal modifyLeverMultiplier) {
        this.modifyLeverMultiplier = modifyLeverMultiplier;
    }
}
