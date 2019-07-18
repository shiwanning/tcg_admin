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
	
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private OperatorProfile profile;

	@Column(name = "BASE_MERCHANT_CODE")
	private String baseMerchantCode;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name = "IS_NEED_RESET")
	private Integer isNeedReset;

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


    public OperatorProfile getProfile() {
        return profile;
    }

    public void setProfile(OperatorProfile profile) {
        this.profile = profile;
    }

	public String getBaseMerchantCode() {
		return baseMerchantCode;
	}

	public void setBaseMerchantCode(String baseMerchantCode) {
		this.baseMerchantCode = baseMerchantCode;
	}

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public String getUpdateOperator() {
		return updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

	public Integer getIsNeedReset() {
		return isNeedReset;
	}

	public void setIsNeedReset(Integer isNeedReset) {
		this.isNeedReset = isNeedReset;
	}
}
