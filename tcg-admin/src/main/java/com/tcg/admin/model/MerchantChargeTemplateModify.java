package com.tcg.admin.model;

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
@Table(name = "TAC_MERCHANT_CHARGE_TEMPLATE_MODIFY")
public class MerchantChargeTemplateModify extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_MODIFY", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_MODIFY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_MODIFY")
    @Column(name = "RID", nullable = false, precision = 0)
    private Long rid;

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @Basic
    @Column(name = "TEMPLATE_TYPE", nullable = false, precision = 0)
    private int templateType;

    @Basic
    @Column(name = "STATUS", nullable = false, precision = 0)
    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_RID")
    private MerchantChargeTemplate merchantChargeTemplate;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_RID", referencedColumnName="RID", insertable = false, updatable = false)
    @OrderBy("RID")
    private List<MerchantChargeTemplateDetailModify> detailList;


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


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MerchantChargeTemplateDetailModify> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<MerchantChargeTemplateDetailModify> detailList) {
        this.detailList = detailList;
    }

    public MerchantChargeTemplate getMerchantChargeTemplate() {
        return merchantChargeTemplate;
    }

    public void setMerchantChargeTemplate(MerchantChargeTemplate merchantChargeTemplate) {
        this.merchantChargeTemplate = merchantChargeTemplate;
    }
}
