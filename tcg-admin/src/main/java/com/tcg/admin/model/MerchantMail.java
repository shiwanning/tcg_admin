package com.tcg.admin.model;

import org.hibernate.annotations.Type;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 客戶端 DB entity
 *
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_MERCHANT_MAIL")
@SequenceGenerator(name = "seq_us_merchant_mail", sequenceName = "SEQ_US_MERCHANT_MAIL", allocationSize = 1)
public class MerchantMail extends BaseEntity {

    /**
     * 品牌 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_us_merchant_mail")
    @Column(name = "MERCHANT_ID")
    private Integer merchantId;

    @Column(name = "MERCHANT_CODE")
    private String merchantCode;

    @Column(name = "SMTP_HOST")
    private String smtpHost;

    @Column(name = "SMTP_PORT")
    private String smtpPort;

    @Column(name = "SMTP_USER")
    private String smtpUser;

    @Column(name = "SMTP_PWD")
    private String smtpPassword;

    @Column(name = "IS_AUTH")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isAuth;

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public Boolean getAuth() {
        return isAuth;
    }

    public void setAuth(Boolean auth) {
        isAuth = auth;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }
}
