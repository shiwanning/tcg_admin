package com.tcg.admin.to.request;

public class MerchantCreateTo {
	
	private Integer merchantId;
	private Integer usMerchantId;
	private String merchantCode;
	private String merchantName;
	private String merchantType;
	private Integer parentId;
	private String upline;
	private Integer customerId;
	private String currency;
	
	private Integer companyCustomerId;
	private Integer companyUsMerchantId;
	private String companyName;
	
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
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getCompanyCustomerId() {
		return companyCustomerId;
	}
	public void setCompanyCustomerId(Integer companyCustomerId) {
		this.companyCustomerId = companyCustomerId;
	}
	public Integer getCompanyUsMerchantId() {
		return companyUsMerchantId;
	}
	public void setCompanyUsMerchantId(Integer companyUsMerchantId) {
		this.companyUsMerchantId = companyUsMerchantId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
