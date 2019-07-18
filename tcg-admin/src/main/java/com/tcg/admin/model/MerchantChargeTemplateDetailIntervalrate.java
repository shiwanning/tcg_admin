package com.tcg.admin.model;

import com.tcg.admin.common.exception.AdminServiceBaseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
@Table(name = "TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE")
public class MerchantChargeTemplateDetailIntervalrate extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE", sequenceName = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAC_MERCHANT_CHARGE_TEMPLATE_DETAIL_INTERVALRATE")
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
    private MerchantChargeTemplateDetail merchantChargeTemplateDetail;


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

    public MerchantChargeTemplateDetail getMerchantChargeTemplateDetail() {
        return merchantChargeTemplateDetail;
    }

    public void setMerchantChargeTemplateDetail(MerchantChargeTemplateDetail merchantChargeTemplateDetail) {
        this.merchantChargeTemplateDetail = merchantChargeTemplateDetail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            final Class bcc = this.getClass();
            Field[] fields = bcc.getDeclaredFields();
            for (Field f : fields) {
                String fieldName = f.getName();
                if (fieldName.contains("Amount") || fieldName.contains("rate")) {
                    String getMethodName = "get".concat(fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1, fieldName.length())));
                    final Method bcGetMethod = bcc.getMethod(getMethodName);
                    Object value = bcGetMethod.invoke(this);
                    sb.append(fieldName).append("=").append(value).append(",");
                }
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(e.getMessage());
        }
        return sb.delete(sb.length()-1, sb.length()).toString();
    }
}
