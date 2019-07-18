package com.tcg.admin.model;

import java.math.BigDecimal;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Cacheable(value = true)
@Entity
@Table(name = "TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_MODIFY")
public class MerchantChargeTemplateDetailModify extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_MODIFY", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_MODIFY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_MODIFY")
    @Column(name = "RID", nullable = false, precision = 0)
    private Long rid;

    @Basic
    @Column(name = "DETAIL_TYPE", nullable = false, precision = 0)
    private int detailType;

    @Basic
    @Column(name = "CODE", nullable = true, length = 200)
    private String code;

    @Basic
    @Column(name = "PRODUCT", nullable = true, length = 200)
    private String product;

    @Basic
    @Column(name = "SUB_PRODUCT", nullable = true, length = 200)
    private String subProduct;

    @Basic
    @Column(name = "RANGE_RATE_FLAG", nullable = true, precision = 0)
    private Integer rangeRateFlag;

    @Basic
    @Column(name = "WIN_LOSS_CALCULATE_FLAG", nullable = true, precision = 0)
    private Integer winLossCalculateFlag;

    @Basic
    @Column(name = "ACCUMULATE_NEGATIVE", nullable = true, precision = 0)
    private Integer accumulateNegative;

    @Basic
    @Column(name = "RATE_TYPE", nullable = true, precision = 0)
    private Integer rateType;

    @Basic
    @Column(name = "RATE", nullable = true, precision = 0)
    private BigDecimal rate;

    @Basic
    @Column(name = "CHARGE", nullable = true, precision = 0)
    private BigDecimal charge;

    @Basic
    @Column(name = "REMARK", nullable = true, length = 200)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_RID")
    private MerchantChargeTemplateModify merchantChargeTemplateModify;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "DETAIL_RID", referencedColumnName="RID", insertable = false, updatable = false)
    @OrderBy("RID")
    private List<MerchantChargeTemplateDetailIntervalrateModify> intervalrateList;


    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }


    public int getDetailType() {
        return detailType;
    }

    public void setDetailType(int detailType) {
        this.detailType = detailType;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }


    public String getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(String subProduct) {
        this.subProduct = subProduct;
    }


    public Integer getRangeRateFlag() {
        return rangeRateFlag;
    }

    public void setRangeRateFlag(Integer rangeRateFlag) {
        this.rangeRateFlag = rangeRateFlag;
    }


    public Integer getWinLossCalculateFlag() {
        return winLossCalculateFlag;
    }

    public void setWinLossCalculateFlag(Integer winLossCalculateFlag) {
        this.winLossCalculateFlag = winLossCalculateFlag;
    }


    public Integer getAccumulateNegative() {
        return accumulateNegative;
    }

    public void setAccumulateNegative(Integer accumulateNegative) {
        this.accumulateNegative = accumulateNegative;
    }


    public Integer getRateType() {
        return rateType;
    }

    public void setRateType(Integer rateType) {
        this.rateType = rateType;
    }


    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }


    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public MerchantChargeTemplateModify getMerchantChargeTemplateModify() {
        return merchantChargeTemplateModify;
    }

    public void setMerchantChargeTemplateModify(MerchantChargeTemplateModify merchantChargeTemplateModify) {
        this.merchantChargeTemplateModify = merchantChargeTemplateModify;
    }

    public List<MerchantChargeTemplateDetailIntervalrateModify> getIntervalrateList() {
        return intervalrateList;
    }

    public void setIntervalrateList(List<MerchantChargeTemplateDetailIntervalrateModify> intervalrateList) {
        this.intervalrateList = intervalrateList;
    }
}
