package com.tcg.admin.model;

import java.math.BigDecimal;

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
@Table(name = "TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE_MODIFY")
public class MerchantChargeTemplateDetailIntervalrateModify extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE_MODIFY", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE_MODIFY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE_MODIFY")
    @Column(name = "RID", nullable = false, precision = 0)
    private Long rid;

    @Basic
    @Column(name = "MIN_AMOUNT", nullable = false, precision = 0)
    private BigDecimal minAmount;

    @Basic
    @Column(name = "MAX_AMOUNT", nullable = false, precision = 0)
    private BigDecimal maxAmount;

    @Basic
    @Column(name = "RATE", nullable = false, precision = 0)
    private BigDecimal rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DETAIL_RID")
    private MerchantChargeTemplateDetailModify merchantChargeTemplateDetailModify;


    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }


    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }


    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }


    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public MerchantChargeTemplateDetailModify getMerchantChargeTemplateDetailModify() {
        return merchantChargeTemplateDetailModify;
    }

    public void setMerchantChargeTemplateDetailModify(MerchantChargeTemplateDetailModify merchantChargeTemplateDetailModify) {
        this.merchantChargeTemplateDetailModify = merchantChargeTemplateDetailModify;
    }
}
