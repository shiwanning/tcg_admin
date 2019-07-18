package com.tcg.admin.to;

import java.math.BigDecimal;
import java.util.Date;

public class MerchantChargeCreditReviewTo {

    private Long rid;
    private String merchantCode;
    private BigDecimal leverMultiplier;
    private BigDecimal modifyLeverMultiplier;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String createOperatorName;
    private String updateOperatorName;
    private Long cashPledge;

    public Long getCashPledge() {
        return cashPledge;
    }

    public void setCashPledge(Long cashPledge) {
        this.cashPledge = cashPledge;
    }

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

    public BigDecimal getModifyLeverMultiplier() {
        return modifyLeverMultiplier;
    }

    public void setModifyLeverMultiplier(BigDecimal modifyLeverMultiplier) {
        this.modifyLeverMultiplier = modifyLeverMultiplier;
    }
}
