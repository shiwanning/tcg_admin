package com.tcg.admin.model;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Cacheable(value = true)
@Entity
@Table(name = "TAC_MERCHANT_CHARGE_TEMPLATE_REVIEW")
public class MerchantChargeTemplateReview extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_REVIEW", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_REVIEW", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_REVIEW")
    @Column(name = "RID", nullable = false, precision = 0)
    private Long rid;

    @Basic
    @Column(name = "STATUS", nullable = false, precision = 0)
    private int status;

    @Basic
    @Column(name = "REMARK", nullable = false, length = 200)
    private String remark;

    @Basic
    @Column(name = "REVIEW_COMMENT", nullable = true, length = 200)
    private String reviewComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFY_RID")
    private MerchantChargeTemplateModify merchantChargeTemplateModify;


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

    public MerchantChargeTemplateModify getMerchantChargeTemplateModify() {
        return merchantChargeTemplateModify;
    }

    public void setMerchantChargeTemplateModify(MerchantChargeTemplateModify merchantChargeTemplateModify) {
        this.merchantChargeTemplateModify = merchantChargeTemplateModify;
    }
}
