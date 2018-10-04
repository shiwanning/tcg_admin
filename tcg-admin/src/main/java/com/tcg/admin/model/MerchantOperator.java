package com.tcg.admin.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_MERCHANT_OPERATOR")
public class MerchantOperator extends BaseEntity {

    private static final long serialVersionUID = 5895721162753430593L;

    /** 主鍵 */
    @Id
    @SequenceGenerator(name = "SEQ_MERCHANT_OPERATOR", sequenceName = "SEQ_MERCHANT_OPERATOR", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MERCHANT_OPERATOR")
    @Column(name = "SEQ_ID")
    private Integer seqId;

    /** 外部鍵(部門) */
    @Column(name = "MERCHANT_ID")
    private Integer merchantId;

    /** 外部鍵(operator) */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

}
