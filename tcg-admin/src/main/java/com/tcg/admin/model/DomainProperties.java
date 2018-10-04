package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "DOMAIN_PROPERTIES")
public class DomainProperties extends BaseAuditEntity {
	
    @Id
    @SequenceGenerator(name = "SEQ_DOMAIN_PROPERTIES", sequenceName = "SEQ_DOMAIN_PROPERTIES", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOMAIN_PROPERTIES")
    @Column(name = "RID")
    private Long rid;
    
    @Column(name = "DOMAIN")
    private String domain;
    
    @Column(name = "LOGIN_PASSWORD")
    @Type(type="yes_no")
    private Boolean loginPassword;
    
    @Column(name = "LOGIN_GOOGLE_OTP")
    @Type(type="yes_no")
    private Boolean loginGoogleOtp;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(Boolean loginPassword) {
		this.loginPassword = loginPassword;
	}

	public Boolean getLoginGoogleOtp() {
		return loginGoogleOtp;
	}

	public void setLoginGoogleOtp(Boolean loginGoogleOtp) {
		this.loginGoogleOtp = loginGoogleOtp;
	}
	
}
