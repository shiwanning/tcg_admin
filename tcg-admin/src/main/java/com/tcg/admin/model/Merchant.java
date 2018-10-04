package com.tcg.admin.model;

import javax.persistence.*;

/**
 * <p>Title: com.tcg.admin.model.Merchant</p>
 * <p>Description: 後台管理員品牌資料表</p>
 * @version 1.0
 */
@Entity
@Table(name="ADMIN_MERCHANT")
public class Merchant extends BaseEntity {

	private static final long serialVersionUID = -7726828549148645271L;

	/**
	 * pKey
	 */
	@Id
	@Column(name = "MERCHANT_ID")
	private Integer merchantId;

	@Column(name = "US_MERCHANT_ID")
	private Integer usMerchantId;


	/**
	 * 品牌代碼
	 */
	@Column(name = "MERCHANT_CODE")
	private String merchantCode;

	/**
	 * 品牌名稱
	 */
	@Column(name = "MERCHANT_NAME")
	private String merchantName;

	@Column(name = "MERCHANT_TYPE")
	private String merchantType;

	/**
	 * 狀態
	 */
	@Column(name = "STATUS")
	private Integer status;

	/**
	 * 上層品牌ID
	 */
	@Column(name = "PARENT_ID")
	private Integer parentId;

	@Column(name = "UPLINE")
	private String upline;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@Column(name = "CREATE_OPERATOR")
	private Integer createOperator;
	
	@Column(name = "CURRENCY")
	private String currency;


	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getUsMerchantId() {
		return usMerchantId;
	}

	public void setUsMerchantId(Integer usMerchantId) {
		this.usMerchantId = usMerchantId;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getUpline() {
		return upline;
	}

	public void setUpline(String upline) {
		this.upline = upline;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Integer getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(Integer createOperator) {
		this.createOperator = createOperator;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	


}

