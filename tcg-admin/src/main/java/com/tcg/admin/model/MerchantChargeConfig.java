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
@Table(name = "TAC_MERCHANT_CHARGE_CONFIG")
public class MerchantChargeConfig extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_CONFIG", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_CONFIG", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_CONFIG")
    @Column(name = "RID", nullable = false, precision = 0)
    private Long rid;

    @Basic
    @Column(name = "MERCHANT_CODE", nullable = false, length = 32)
    private String merchantCode;

    @Basic
    @Column(name = "TEMPLATE_RID", nullable = false)
    private Long templateRid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_RID", insertable = false, updatable = false)
    private MerchantChargeTemplate merchantChargeTemplate;

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

    public Long getTemplateRid() {
        return templateRid;
    }

    public void setTemplateRid(Long templateRid) {
        this.templateRid = templateRid;
    }

    public MerchantChargeTemplate getMerchantChargeTemplate() {
        return merchantChargeTemplate;
    }

    public void setMerchantChargeTemplate(MerchantChargeTemplate merchantChargeTemplate) {
        this.merchantChargeTemplate = merchantChargeTemplate;
    }
}
