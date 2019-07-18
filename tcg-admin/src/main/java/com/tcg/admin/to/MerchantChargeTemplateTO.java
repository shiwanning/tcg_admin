package com.tcg.admin.to;

import java.util.Date;
import java.util.List;

public class MerchantChargeTemplateTO {

    private Long rid;
    private String name;
    private int templateType;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String createOperatorName;
    private String updateOperatorName;
    private Boolean isWaitingForReview;
    private List<MerchantChargeDetailTO> detailList;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemplateType() {
        return templateType;
    }

    public void setTemplateType(int templateType) {
        this.templateType = templateType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<MerchantChargeDetailTO> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<MerchantChargeDetailTO> detailList) {
        this.detailList = detailList;
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

    public Boolean getWaitingForReview() {
        return isWaitingForReview;
    }

    public void setWaitingForReview(Boolean waitingForReview) {
        isWaitingForReview = waitingForReview;
    }
}
