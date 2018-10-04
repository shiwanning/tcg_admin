package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ADMIN_MERCHANT_WHITELIST_IP")
public class MerchantWhitelistIp extends BaseAuditEntity{
    
    @Id
    @SequenceGenerator(name = "SEQ_ADMIN_MERCHANT_WHITELIST_IP", sequenceName = "SEQ_ADMIN_MERCHANT_WHITELIST_IP", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADMIN_MERCHANT_WHITELIST_IP")
    @Column(name = "RID")
    private Long rid;
    
    @Column(name = "MERCHANT_CODE")
    private String merchantCode;
    
    @Column(name = "IP")
    private String ip;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
