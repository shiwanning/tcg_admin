package com.tcg.admin.model;


import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>
 * Title: com.tcg.admin.model.Operator
 * </p>
 * <p>
 * Description: 營運管理人員資料表
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "US_OPERATOR")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "roles" })
@SequenceGenerator(name = "seq_operator", sequenceName = "SEQ_OPERATOR", allocationSize = 1)
@Cacheable(true)
public class Operator extends UserPrincipal {
	/** 管理員ID */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_operator")
	@Column(name = "OPERATOR_ID")
	private Integer operatorId;
	
	/** 管理員名稱 */
	@Column(name = "OPERATOR_NAME")
	private String operatorName;

//	/** 部門ID */
//	@Column(name = "DEPT_ID")
//	private Integer deptId;

	/** 暱稱 */
	@Column(name = "NICKNAME")
	private String nickname;

//    @Column(name = "EMAIL")
//    private String email;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private OperatorProfile profile;

	@Column(name = "BASE_MERCHANT_CODE")
	private String baseMerchantCode;

    public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

//	public Integer getMerchantId() {
//		return deptId;
//	}
//
//	public void setMerchantId(Integer deptId) {
//		this.deptId = deptId;
//	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

//	public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }


    public OperatorProfile getProfile() {
        return profile;
    }

    public void setProfile(OperatorProfile profile) {
        this.profile = profile;
//        profile.setOperator(this);
    }

	public String getBaseMerchantCode() {
		return baseMerchantCode;
	}

	public void setBaseMerchantCode(String baseMerchantCode) {
		this.baseMerchantCode = baseMerchantCode;
	}
}
