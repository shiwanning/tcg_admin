package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ADMIN_MERCHANT_PROPERTIES")
public class MerchantProperties extends BaseAuditEntity {
    
    @Id
    @SequenceGenerator(name = "SEQ_ADMIN_MERCHANT_PROPERTIES", sequenceName = "SEQ_ADMIN_MERCHANT_PROPERTIES", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADMIN_MERCHANT_PROPERTIES")
    @Column(name = "RID")
    private Long rid;
    
    @Column(name = "MERCHANT_CODE")
    private String merchantCode;
    
    @Column(name = "WHITELIST_FUNCTION")
    private String whitelistFunction;

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

    public String getWhitelistFunction() {
        return whitelistFunction;
    }

    public void setWhitelistFunction(String whitelistFunction) {
        this.whitelistFunction = whitelistFunction;
    }
    
}
