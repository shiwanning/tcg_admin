package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * <p>Title: com.tcg.admin.model.OperatorProfile</p>
 * <p>Description: 帳戶基本資料</p>
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_OPERATOR_PROFILE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "operator" })
@GenericGenerator(name = "operatorProfileIdGenerator", strategy = "foreign", parameters = { @Parameter(name = "property", value = "operator") })
public class OperatorProfile extends BaseEntity {

	private static final long serialVersionUID = -3051234907197752306L;

	/**
	 * 帳戶
	 */
	@Id
//	@GeneratedValue(generator = "operatorProfileIdGenerator")
	@Column(name = "OPERATOR_ID")
	private Integer operatorId;


    /**
	 * 密碼最後修改日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PASSWD_LAST_MODIFY_DATE")
	private Date passwdLastModifyDate;

	/**
	 * 手機號碼
	 */
	@Column(name = "MOBILE_NO")
	private String mobileNo;

	/**
	 * EMAIL
	 */
	@Column(name = "EMAIL")
	private String email;
	
    @Column(name = "PHONE")
    private String phone;


	/**
	 * 用戶目前是否登入 0：false , 1: true
	 */
	@Column(name = "LOGIN")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean login;

    @Column(name = "NOTI_SOUND")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean notiSound = Boolean.TRUE;


    /**
	 * 最後一次登入IP
	 */
	@Column(name = "LAST_LOGIN_IP")
	private String lastLoginIP;
	
	/**
	 * 不活躍天數
	 */
	@Column(name = "NO_ACTIVE")
	private Integer noActive;

	@Column(name = "PAGE_SIZE")
    private  Integer pageSize = 20;

    /**
	 * 最後登入時間
	 */
	@Column(name = "LAST_LOGIN_TIME")
	private Date lastLoginTime;

	/**
	 * 最後登出時間
	 */
	@Column(name = "LAST_LOGOUT_TIME")
	private Date lastLogoutTime;

//	@OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
//	private Operator operator;

	public Date getPasswdLastModifyDate() {
		return passwdLastModifyDate;
	}

	public void setPasswdLastModifyDate(Date passwdLastModifyDate) {
		this.passwdLastModifyDate = passwdLastModifyDate;
	}

    public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getLogin() {
		return login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public Integer getNoActive() {
		return noActive;
	}

	public void setNoActive(Integer noActive) {
		this.noActive = noActive;
	}

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}


	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}


	public Date getLastLogoutTime() {
		return lastLogoutTime;
	}


	public void setLastLogoutTime(Date lastLogoutTime) {
		this.lastLogoutTime = lastLogoutTime;
	}

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getNotiSound() {
        return notiSound;
    }

    public void setNotiSound(Boolean notiSound) {
        this.notiSound = notiSound;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}    
    
}
