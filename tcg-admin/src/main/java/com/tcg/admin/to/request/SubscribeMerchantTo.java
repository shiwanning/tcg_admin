package com.tcg.admin.to.request;

import java.util.List;

public class SubscribeMerchantTo {

	private String merchantCode;
	private List<String> operatorNames;
	private List<Integer> roleIds;

	public List<Integer> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public List<String> getOperatorNames() {
		return operatorNames;
	}
	public void setOperatorNames(List<String> operatorNames) {
		this.operatorNames = operatorNames;
	}
}
