package com.tcg.admin.to;

import java.util.Date;

public class MerchantChargeTemplateReviewTO {

    private Long rid;
    private int status;
    private String remark;
    private String reviewComment;

    private Date createTime;
    private Date updateTime;
    private String createOperatorName;
    private String updateOperatorName;

    private MerchantChargeTemplateModifyTO merchantChargeTemplate;
    private MerchantChargeTemplateModifyTO merchantChargeTemplateModify;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
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

    public MerchantChargeTemplateModifyTO getMerchantChargeTemplate() {
        return merchantChargeTemplate;
    }

    public void setMerchantChargeTemplate(MerchantChargeTemplateModifyTO merchantChargeTemplate) {
        this.merchantChargeTemplate = merchantChargeTemplate;
    }

    public MerchantChargeTemplateModifyTO getMerchantChargeTemplateModify() {
        return merchantChargeTemplateModify;
    }

    public void setMerchantChargeTemplateModify(MerchantChargeTemplateModifyTO merchantChargeTemplateModify) {
        this.merchantChargeTemplateModify = merchantChargeTemplateModify;
    }
}
